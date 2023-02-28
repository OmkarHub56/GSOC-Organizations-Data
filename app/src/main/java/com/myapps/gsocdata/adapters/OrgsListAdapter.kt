package com.myapps.gsocdata.adapters

import com.myapps.gsocdata.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapps.gsocdata.activities.OneOrgActivity
import com.myapps.gsocdata.models.OneOrg


class OrgsListAdapter(list: ArrayList<OneOrg>,context: Context) :
    RecyclerView.Adapter<OrgsListViewHolder>() {

    var mList : ArrayList<OneOrg> = list
    var context : Context = context
    var set2023 : HashSet<String>?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrgsListViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.one_org_layout, parent, false)

        return OrgsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrgsListViewHolder, position: Int) {
        holder.orgName.text= mList[position].name
        Glide.with(context).load(mList[position].image_url).into(holder.orgLogo)
        holder.orgType.text= mList[position].categoy

        holder.itemView.setOnClickListener {
//            val intent = Intent(this@HomeActivity,ProfileActivity::class.java)
            val intent=Intent(context, OneOrgActivity::class.java)
            intent.putExtra("org_info",mList[position])
            intent.putExtra("2023_org_set",set2023)
            context.startActivity(intent)
        }

    }

    fun add2023Orgs(set : HashSet<String>){
        set2023=set
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}

class OrgsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val orgLogo: ImageView =itemView.findViewById(R.id.org_logo)
    val orgName:TextView=itemView.findViewById(R.id.org_name)
    val orgType:TextView=itemView.findViewById(R.id.org_type)
}