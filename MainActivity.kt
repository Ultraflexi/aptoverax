package com.example.aptoverax

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.mcxross.kaptos.Aptos
import xyz.mcxross.kaptos.models.Account
import xyz.mcxross.kaptos.models.TransactionPayload
import xyz.mcxross.kaptos.types.aptos.Function
import xyz.mcxross.kaptos.types.aptos.TransactionArgumentAddress
import xyz.mcxross.kaptos.types.aptos.TransactionArgumentU64

class MainActivity : AppCompatActivity() {

    private lateinit var amountInput: EditText
    private lateinit var transferButton: Button
    private lateinit var resultView: TextView

    // USER private key
    private val privateKeyHex = ""

    private val aptos = Aptos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
	
        amountInput = findViewById(R.id.amountInput)
        transferButton = findViewById(R.id.transferButton)
        resultView = findViewById(R.id.resultView)

        transferButton.setOnClickListener {
            val recipient = "0x5e3bd198f6a58a0d8ab7c5832e788d1e253c9f48d45208e3d4bbe4783583e897"
            val amountStr = amountInput.text.toString().trim()

            if (amountStr.isEmpty()) {
                resultView.text = "Please enter APT amount."
                return@setOnClickListener
            }

            val amountApt = amountStr.toDoubleOrNull()
            if (amountApt == null || amountApt <= 0) {
                resultView.text = "Invalid APT amount specified."
                return@setOnClickListener
            }

            // Convert APT to Octas
            val amountOctas = (amountApt * 100_000_000).toULong()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val sender = Account.fromPrivateKey(privateKeyHex)

                    val moduleAddress = "0x5e3bd198f6a58a0d8ab7c5832e788d1e253c9f48d45208e3d4bbe4783583e897"
                    val entryFunction = Function(
                        moduleAddress = moduleAddress,
                        moduleName = "aptos_transfer",
                        functionName = "transfer_aptos",
                        typeArgs = emptyList(),
                        args = listOf(
                            TransactionArgumentAddress(recipient),
                            TransactionArgumentU64(amountOctas)
                        )
                    )

                    val payload = TransactionPayload.EntryFunction(entryFunction)
                    val txnRequest = aptos.transaction.build {
                        sender = sender.accountAddress
                        this.payload = payload
                    }

                    val (authenticator, signedTxn) = aptos.transaction.sign(sender, txnRequest)
                    val result = aptos.transaction.submit(signedTxn, authenticator)

                    withContext(Dispatchers.Main) {
                        resultView.text = "Transaction submitted: ${result.hash}"
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        resultView.text = "Transaction Error: ${e.message}"
                    }
                }
            }
        }
    }
}
