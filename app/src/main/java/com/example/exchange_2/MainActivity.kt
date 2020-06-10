package com.example.exchange_2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.BreakIterator
import java.util.*

class MainActivity : AppCompatActivity() {
    var data: BreakIterator? = null
    var keysList: MutableList<String>? = null
    var toCurrency: Spinner? = null
    var fromCurrency:Spinner? = null
    var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toCurrency = findViewById<View>(R.id.planets_spinner) as Spinner
        fromCurrency = findViewById<View>(R.id.planets_spinner1) as Spinner
        val edtValue = findViewById<View>(R.id.editText4) as EditText
        val btnConvert = findViewById<View>(R.id.button) as Button
        val reverse = findViewById<View>(R.id.reverse) as ImageButton
        reverse.setOnClickListener {
            val toCurr = toCurrency!!.selectedItem.toString()
            val fromCurr: String = fromCurrency!!.getSelectedItem().toString()
            toCurrency!!.setSelection((toCurrency!!.adapter as ArrayAdapter<String?>).getPosition(fromCurr))
            fromCurrency!!.setSelection((fromCurrency!!.getAdapter() as ArrayAdapter<String?>).getPosition(toCurr))
        }
        textView = findViewById<View>(R.id.textView7) as TextView
        try {
            loadConvTypes()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        btnConvert.setOnClickListener {
            if (!edtValue.text.toString().isEmpty()) {
                val toCurr = toCurrency!!.selectedItem.toString()
                val fromCurr: String = fromCurrency!!.getSelectedItem().toString()
                val edtvalue = java.lang.Double.valueOf(edtValue.text.toString())
                Toast.makeText(this@MainActivity, "Please Wait..", Toast.LENGTH_SHORT).show()
                try {
                    convertCurrency(fromCurr, toCurr, edtvalue)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "Please Enter a Value to Convert..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    fun loadConvTypes() {
        val url = "https://api.exchangeratesapi.io/latest"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                val mMessage = e.message.toString()
                Log.w("failure Response", mMessage)
                Toast.makeText(this@MainActivity, mMessage, Toast.LENGTH_SHORT).show()
            }

            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                val mMessage = response.body().string()
                runOnUiThread { //Toast.makeText(MainActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                    try {
                        val obj = JSONObject(mMessage)
                        val b = obj.getJSONObject("rates")
                        val keysToCopyIterator: Iterator<*> = b.keys()
                        keysList = ArrayList()
                        while (keysToCopyIterator.hasNext()) {
                            val key = keysToCopyIterator.next() as String
                            (keysList as ArrayList<String>).add(key)
                        }
                        val spinnerArrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, keysList as ArrayList<String>)
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        val spinnerArrayAdapter1 = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, keysList as ArrayList<String>)
                        spinnerArrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1)
                        fromCurrency!!.setAdapter(spinnerArrayAdapter1)
                        toCurrency!!.adapter = spinnerArrayAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    @Throws(IOException::class)
    fun convertCurrency(fromCurr: String?, toCurr: String?, euroVlaue: Double) {
        val url = "https://api.exchangeratesapi.io/latest"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                val mMessage = e.message.toString()
                Log.w("failure Response", mMessage)
                Toast.makeText(this@MainActivity, mMessage, Toast.LENGTH_SHORT).show()
            }

            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                val mMessage = response.body().string()
                runOnUiThread { //Toast.makeText(MainActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                    try {
                        val obj = JSONObject(mMessage)
                        val b = obj.getJSONObject("rates")
                        val `val` = b.getString(toCurr)
                        val val1 = b.getString(fromCurr)
                        val output = euroVlaue * java.lang.Double.valueOf(val1) / java.lang.Double.valueOf(`val`)
                        textView!!.text = output.toString()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}