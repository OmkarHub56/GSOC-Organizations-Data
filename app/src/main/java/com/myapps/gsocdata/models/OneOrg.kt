package com.myapps.gsocdata.models

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class OrgList2023 : java.io.Serializable{

    @SerializedName("organizations") var organizations : ArrayList<OneOrg>? = ArrayList()

}
class OneOrg : java.io.Serializable{

    @SerializedName("name") var name : String? = null
    @SerializedName("url") var url : String? = null
    @SerializedName("image_url") var image_url : String?=null
    @SerializedName("image_background_color") var image_background_color : String?=null
    @SerializedName("description") var description : String?=null
    @SerializedName("category") var categoy : String?=null

    @SerializedName("topics") var topics : List<String>? = ArrayList()
    @SerializedName("technologies") var technologies : List<String>? = ArrayList()
    @SerializedName("years") var years : Years? =null

    @SerializedName("irc_channel") var irc_channel : String?=null
    @SerializedName("contact_email") var contact_email : String?=null
    @SerializedName("mailing_list") var mailing_list : String?=null
    @SerializedName("twitter_url") var twitter_url : String?=null
    @SerializedName("blog_url") var blog_url : String?=null
    @SerializedName("facebook_url") var facebook_url : String?=null

    fun fillYearsData(){
        years!!.fillYearsData(name!!)
    }

}

class Years : java.io.Serializable{

    // variable name cant start with number
    @SerializedName("2016") var y2016 : YearData? = null
    @SerializedName("2017") var y2017 : YearData? = null
    @SerializedName("2018") var y2018 : YearData? = null
    @SerializedName("2019") var y2019 : YearData? = null
    @SerializedName("2020") var y2020 : YearData? = null
    @SerializedName("2021") var y2021 : YearData? = null
    @SerializedName("2022") var y2022 : YearData? = null
    var yearsDataList : ArrayList<YearData?> = ArrayList()
    var yearsParticipateIn : ArrayList<String> = ArrayList()
    var yearCount=0
    var totalProjects=0
    var avgProjects=0
    var minProjects=-1

    fun fillYearsData(str:String){

        yearsDataList.add(if(y2016!=null)y2016 else null)
        if(y2016!=null) yearsParticipateIn.add("2016")
        yearsDataList.add(if(y2017!=null)y2017 else null)
        if(y2017!=null) yearsParticipateIn.add("2017")
        yearsDataList.add(if(y2018!=null)y2018 else null)
        if(y2018!=null) yearsParticipateIn.add("2018")
        yearsDataList.add(if(y2019!=null)y2019 else null)
        if(y2019!=null) yearsParticipateIn.add("2019")
        yearsDataList.add(if(y2020!=null)y2020 else null)
        if(y2020!=null) yearsParticipateIn.add("2020")
        yearsDataList.add(if(y2021!=null)y2021 else null)
        if(y2021!=null) yearsParticipateIn.add("2021")
        yearsDataList.add(if(y2022!=null)y2022 else null)
        if(y2022!=null) yearsParticipateIn.add("2022")
        for(i in yearsDataList.indices){
            if(yearsDataList[i]!=null){
                yearCount++
                totalProjects+=yearsDataList[i]!!.projects!!.size
                if(minProjects==-1){
                    minProjects=yearsDataList[i]!!.projects!!.size
                }
                else{
                    minProjects=Math.min(minProjects,yearsDataList[i]!!.projects!!.size)
                }
            }
        }
        if(yearCount==0){
            Log.i("helpn",str)
        }
        avgProjects=if(yearCount!=0) totalProjects/yearCount else 0
    }
}

class YearData : java.io.Serializable{
    @SerializedName("projects_url") var projects_url : String? = null
    @SerializedName("num_projects") var num_projects : Int? = null
    @SerializedName("projects") var  projects : List<Project>? = ArrayList()
}

class Project : java.io.Serializable{

    @SerializedName("title") var title : String? = null
    @SerializedName("short_description") var shortDescription : String? = null
    @SerializedName("description") var description : String? = null
    @SerializedName("student_name") var studentName : String? = null
    @SerializedName("code_url") var codeUrl : String? = null
    @SerializedName("project_url") var projectUrl : String? = null

}

internal object ApiUtilities {
    private var retrofit: Retrofit? = null
    val apiInterface: ApiInterface

        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit!!.create(ApiInterface::class.java)
        }
}
//private static Retrofit retrofit=null;
//
//public static ApiInterface2 getApiInterface(){
//    if(retrofit==null){
//        System.out.println("lolkn");
//        retrofit = new Retrofit.Builder().baseUrl(ApiInterface2.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
//    }
//    return retrofit.create(ApiInterface2.class);
//}

interface ApiInterface {
    @GET("organizations.json")
    fun getFullOrglist(
    ): Call<ArrayList<OneOrg>>?

    @GET("2023.json")
    fun get2023Orglist(
    ): Call<OrgList2023>?

}

const val BASE_URL = "https://api.gsocorganizations.dev/"






