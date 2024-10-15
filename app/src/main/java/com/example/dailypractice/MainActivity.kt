package com.example.dailypractice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dailypractice.ui.theme.DailyPracticeTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.io.File
import java.math.BigDecimal

class MainActivity : ComponentActivity() {

    private val  TAG ="EtheamWallet"
    private val INFURA_URL = "https://rinkeby.infura.io/v3/YOUR_INFURA_PROJECT_ID"
    private val PASSWORD ="YOUR_WALLET_PASSWORD"
    private lateinit var web3j: Web3j

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyPracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        web3j = Web3j.build(HttpService(INFURA_URL))
        Log.d(TAG, "Connected to Ethereum network: ${web3j.web3ClientVersion().send().web3ClientVersion}" )

        try{
            val wallertDirectory = filesDir.path
            val walletName = WalletUtils.generateLightNewWalletFile(PASSWORD, File(wallertDirectory))
            Log.d(TAG, "Created new wallet: $walletName")

            val credintials = WalletUtils.loadCredentials(PASSWORD, "$wallertDirectory/$walletName")
            Log.d(TAG, "Loaded wallet credentials: $credintials")
            checkBalance(credintials.address)
        }catch (e:Exception){
            Log.e(TAG, "Error connecting to Ethereum network: ${e.message}")
        }

    }

    private fun checkBalance(address: String?) {
       CoroutineScope(Dispatchers.IO).launch{
           try{
              val balance = web3j.ethGetBalance(address, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send().balance
               val ethBlance = BigDecimal(balance).divide(BigDecimal(10).pow(18))
               Log.d(TAG, "Balance: $ethBlance")
           }catch (e:Exception){
               Log.e(TAG, "Error connecting to Ethereum network: ${e.message}")
           }
       }

    }
}





@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DailyPracticeTheme {
        Greeting("Android")
    }
}