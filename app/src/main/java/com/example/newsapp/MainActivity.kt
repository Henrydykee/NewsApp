package com.example.newsapp

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    var ProgressBar = progress_bar
    var listData = ArrayList<Data>()
    var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    inner class MyAsyncTask : AsyncTask<String, Void, ArrayList<Data>>(){
        override fun onPostExecute(result: ArrayList<Data>?) {
            if (result != null){
                updateUi(result)
            }
        }
        override fun doInBackground(vararg params: String?): ArrayList<Data> {
            val url = createUrl(params[0])
            var jsonResponse : String? =""
            try {
                jsonResponse = makeHttpRequest(url)
            }catch (e:IOException){
                Log.e("MainActivity","Error connection $e")
            }
            val data = extractFeaturesFromResponse(jsonResponse)
            return  data
        }
    }

  fun updateUi(list: ArrayList<Data>) {
      list_view?.adapter =NewsAdapter(this,list)
      }

    fun extractFeaturesFromResponse(guardianJson: String?): ArrayList<Data> {
        try {
            val baseJsonRsponse = JSONObject(guardianJson)
            val response = baseJsonRsponse.getJSONObject("response")
            val newsArray = response.getJSONArray("results")
            for ( i in 0..9){
               val item = newsArray.getJSONObject(i)
                val sectionName =item.getString("sectionName")
               val webTittle = item.getString("webTittle")
               val webUrl = item.getString("webUrl")
                val data = Data(sectionName,webTittle,webUrl)
                listData.add(data)
            }
        }catch (e:JSONException){
            Log.e("MainActivity","problem prasing news json $e")
        }
        return listData
    }

    fun makeHttpRequest(url: URL?): String{
        var jsonResponse : String =""
        var urlConection : HttpURLConnection? = null
        var inputStream : InputStream? = null
        try {
            urlConection = url?.openConnection() as HttpURLConnection
            urlConection.requestMethod = "GET"
            urlConection.setRequestProperty("Accept","Application/json")
            urlConection.setRequestProperty("api-key","2651ce40-7e01-49a2-929c-ea9bc2e85e6c")
            urlConection.readTimeout =10000
            urlConection.connectTimeout = 15000
            urlConection.connect()
            if (urlConection?.responseCode == 200){
               var inputStream = urlConection.inputStream
                jsonResponse =  readFromStream(inputStream)
            }else{
                Log.i("MainActivity","the code is : ${urlConection.responseCode}")
            }
            urlConection.disconnect()
            inputStream?.close()

        }catch (e:IOException){
            Log.e("MainActivity","Error in response code:"+urlConection?.responseCode)
        }
        return jsonResponse
    }

    private fun readFromStream(inputStream: InputStream?): String {
        var  output = StringBuilder()
        var inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
        val reader = BufferedReader(inputStreamReader)
        var line : String? = reader.readLine()

        while (line!=null){
            output.append(line)
            line = reader.readLine()
        }
        return  output.toString()
    }

    fun createUrl(stringUrl: String?): URL? {
       val url  : URL?
       try {
            url = URL(stringUrl)
       }catch (e: MalformedURLException){
           Log.e("MainActivity", "Error in creating url$e")
           return null
      }
       return null
   }

    fun searchWord(view: View) {
        progress_bar.visibility = View.VISIBLE
        pageNumber =1
        val stringUrl ="https://https://content.guardianapis.com/search?q="+edit_text?.text+"&tag=politics/politics&page="+pageNumber
        listData.clear()
        var myAsyncTask = MyAsyncTask()
        myAsyncTask.execute(stringUrl)
        progress_bar.visibility=View.INVISIBLE
    }

    fun loadMore(view: View) {
        progress_bar.visibility = View.VISIBLE
        pageNumber +=1
        val stringUrl ="https://https://content.guardianapis.com/search?q="+edit_text?.text+"&tag=politics/politics&page="+pageNumber
        val myAsyncTask = MyAsyncTask()
        myAsyncTask.execute(stringUrl)
        progress_bar.visibility = View.INVISIBLE
    }
}
