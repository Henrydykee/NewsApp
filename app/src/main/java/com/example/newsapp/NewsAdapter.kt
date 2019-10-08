package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class NewsAdapter( context:Context, arrayList:ArrayList<Data>) : BaseAdapter() {



    var arrayList: ArrayList<Data> = ArrayList()
    var context:Context?
    init {
        this.arrayList = arrayList
        this.context = context
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view : View
        var holder : ViewHolder
        val inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (p1 == null){
            holder = ViewHolder()

            view = inflater.inflate(R.layout.news_item,p2,false)
            holder.sectionName = view.findViewById(R.id.section_name_text_view)
            holder.webTittle = view.findViewById(R.id.news_tittle_text_view)
            view.tag = holder
        } else{
            view = p1
            holder = p1.tag as ViewHolder
            }
                val sectionNameValue  = holder.sectionName
                sectionNameValue?.text = arrayList[p0].sectionName

                val tittleValue : TextView? = holder.webTittle
                tittleValue?.text = arrayList[p0].webTittle

                tittleValue?.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayList[p0].toString()))
                    context!!.startActivity(browserIntent)
                }

        return  view
    }

    override fun getItem(p0: Int): Any {
        return arrayList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }
    class ViewHolder {
        var sectionName: TextView? = null
        var webTittle : TextView? = null
    }
}