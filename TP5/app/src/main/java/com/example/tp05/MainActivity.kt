package com.example.tp05

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {
    var _txtLogin: EditText? = null
    var _txtPassword: EditText? = null
    var _btnConnexion: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _txtLogin = findViewById<View>(R.id.txtLogin) as EditText
        _txtPassword = findViewById<View>(R.id.txtPassword) as EditText
        _btnConnexion = findViewById<View>(R.id.btnConnexion) as Button
        _btnConnexion!!.setOnClickListener {
            val user = _txtLogin!!.text.toString()
            val passwd = _txtPassword!!.text.toString()
            val background: bg = bg(this@MainActivity)
            background.execute(user, passwd)
        }
    }

    private inner class bg(var c: Context) :
        AsyncTask<String?, Void?, String>() {
        var dialog: AlertDialog? = null
        override fun onPreExecute() {
            dialog = AlertDialog.Builder(c).create()
            dialog!!.setTitle("Etat de connexion")
        }

        protected override fun doInBackground(vararg strings: String): String {
            var result = ""
            val user = strings[0]
            val pass = strings[1]
            val connstr = "http://192.168.1.8/tp05/login.php"
            try {
                val url = URL(connstr)
                val http = url.openConnection() as HttpURLConnection
                http.requestMethod = "POST"
                http.doInput = true
                http.doOutput = true
                val ops = http.outputStream
                val writer = BufferedWriter(OutputStreamWriter(ops, "UTF-8"))
                val data =
                    URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") +
                            "&&" + URLEncoder.encode(
                        "pass",
                        "UTF-8"
                    ) + "=" + URLEncoder.encode(pass, "UTF-8")
                writer.write(data)
                writer.flush()
                writer.close()
                val ips = http.inputStream
                val reader = BufferedReader(InputStreamReader(ips, "ISO-8859-1"))
                var ligne = ""
                while (reader.readLine().also { ligne = it } != null) {
                    result = result + ligne
                    // ou bien result += ligne;
                }
                reader.close()
                ips.close()
                http.disconnect()
                return result
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("error", e.message!!)
            }
            return result
        }

        override fun onPostExecute(s: String) {
            dialog!!.setMessage(s)
            try {
                dialog!!.show()
            } catch (e: Exception) {
                Log.e("errorpost", e.message!!)
            }
            if (s.contains("succes")) {
                val i = Intent()
                i.setClass(c.applicationContext, ProduitsActivity::class.java)
                startActivity(i)
            }
        }
    }
}