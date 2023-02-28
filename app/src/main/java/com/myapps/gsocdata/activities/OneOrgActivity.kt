package com.myapps.gsocdata.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.myapps.gsocdata.R
import com.myapps.gsocdata.databinding.ActivityOneOrgBinding
import com.myapps.gsocdata.models.OneOrg
import com.myapps.gsocdata.models.YearData

class OneOrgActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var binding : ActivityOneOrgBinding
    private lateinit var currentOrg : OneOrg
    private lateinit var orgsSet2023 : HashSet<String>

    private lateinit var listOfTopics : FlexboxLayout
    private lateinit var listOfTechStack : FlexboxLayout

    private lateinit var yearChooseSpinner : AppCompatSpinner
    private lateinit var yearsList : ArrayList<String>
    private lateinit var yearsListAdapter : ArrayAdapter<String>

    var currYear=2016
    var projectsIndexShower : TextView?=null
    var currentProject=0
    var totalProjects=0

    var projectLink = ""
    var projectSourceLink = ""

//    private lateinit var projectsList : ArrayList<YearData>

    var projects2016 : YearData? =null
    var projects2017 : YearData? =null
    var projects2018 : YearData? =null
    var projects2019 : YearData? =null
    var projects2020 : YearData? =null
    var projects2021 : YearData? =null
    var projects2022 : YearData? =null
    var currentSelectedProject : YearData?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOneOrgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentOrg= intent.getSerializableExtra("org_info") as OneOrg
        orgsSet2023= intent.getSerializableExtra("2023_org_set") as HashSet<String>

        binding.mainLl.minimumHeight=resources.displayMetrics.heightPixels

        // setting orgs basic info
        binding.orgName.text=currentOrg.name
        binding.orgType.text=currentOrg.categoy
        Glide.with(this).load(currentOrg.image_url).into(binding.orgLogo)

        binding.orgDescription.text=currentOrg.description
        binding.orgLink.text=currentOrg.url
        Linkify.addLinks(binding.orgLink,Linkify.ALL)

        // filling the org topics in flex layout
        listOfTopics=binding.topicsList
        val list_of_topics=currentOrg.topics
        if (list_of_topics != null) {
            for(i in list_of_topics.indices){
                val topic=layoutInflater.inflate(R.layout.one_topic_or_tech_stack,listOfTopics,false) as TextView
                topic.text=list_of_topics[i]
                listOfTopics.addView(topic)
            }
        }

        // filling the org tech stack in flex layout
        listOfTechStack= binding.techStackList
        val list_of_tech_stach=currentOrg.technologies
        if (list_of_tech_stach != null) {
            for(i in list_of_tech_stach.indices){
                val tech=layoutInflater.inflate(R.layout.one_topic_or_tech_stack,listOfTechStack,false) as TextView
                tech.text=list_of_tech_stach[i]
                listOfTechStack.addView(tech)
            }
        }

        // projects list browsing left and right buttons
        binding.leftProject.setOnClickListener(this)
        binding.rightProject.setOnClickListener(this)

        // the project and project source links
        binding.viewProjectButton.setOnClickListener{
            if(projectLink.isEmpty()){
                Toast.makeText(this,"This org completed no projects in th current selected year",Toast.LENGTH_SHORT).show()
            }
            else{
                val uri = Uri.parse(projectLink)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
        binding.viewSourceCodeButton.setOnClickListener{
            if(projectLink.isEmpty()){
                Toast.makeText(this,"This org completed no projects in th current selected year",Toast.LENGTH_SHORT).show()
            }
            else{
                val uri = Uri.parse(projectSourceLink)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        // to check the years the org has participated in
        yearsList=ArrayList()
        if(currentOrg.years!!.y2016!=null){
            projects2016=currentOrg.years!!.y2016
            yearsList.add("2016")
            binding.cameIn2016.setImageResource(R.drawable.tick_logo)
        }
        if(currentOrg.years!!.y2017!=null){
            projects2017=currentOrg.years!!.y2017
            yearsList.add("2017")
            binding.cameIn2017.setImageResource(R.drawable.tick_logo)
        }
        if(currentOrg.years!!.y2018!=null){
            projects2018=currentOrg.years!!.y2018
            yearsList.add("2018")
            binding.cameIn2018.setImageResource(R.drawable.tick_logo)
        }
        if(currentOrg.years!!.y2019!=null){
            projects2019=currentOrg.years!!.y2019
            yearsList.add("2019")
            binding.cameIn2019.setImageResource(R.drawable.tick_logo)
        }
        if(currentOrg.years!!.y2020!=null){
            projects2020=currentOrg.years!!.y2020
            yearsList.add("2020")
            binding.cameIn2020.setImageResource(R.drawable.tick_logo)
        }
        if(currentOrg.years!!.y2021!=null){
            projects2021=currentOrg.years!!.y2021
            yearsList.add("2021")
            binding.cameIn2021.setImageResource(R.drawable.tick_logo)
        }
        if(currentOrg.years!!.y2022!=null){
            projects2022=currentOrg.years!!.y2022
            yearsList.add("2022")
            binding.cameIn2022.setImageResource(R.drawable.tick_logo)
        }
        if(orgsSet2023.contains(currentOrg.name)){
            binding.cameIn2023.setImageResource(R.drawable.tick_logo)
        }

        projectsIndexShower=binding.projectIndexShower
        yearChooseSpinner=binding.yearChooseSpinner
        yearChooseSpinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(yearsList[p2]==("2016")){
                    currentProject=1
                    totalProjects= projects2016?.projects?.size!!
                    currentSelectedProject=projects2016
                    currYear=2016
                }
                else if(yearsList[p2]==("2017")){
                    currentProject=1
                    totalProjects= projects2017?.projects?.size!!
                    currentSelectedProject=projects2017
                    currYear=2017
                }
                else if(yearsList[p2]==("2018")){
                    currentProject=1
                    totalProjects= projects2018?.projects?.size!!
                    currentSelectedProject=projects2018
                    currYear=2018
                }
                else if(yearsList[p2]==("2019")){
                    currentProject=1
                    totalProjects= projects2019?.projects?.size!!
                    currentSelectedProject=projects2019
                    currYear=2019
                }
                else if(yearsList[p2]==("2020")){
                    currentProject=1
                    totalProjects= projects2020?.projects?.size!!
                    currentSelectedProject=projects2020
                    currYear=2020
                }
                else if(yearsList[p2]==("2021")){
                    currentProject=1
                    totalProjects= projects2021?.projects?.size!!
                    currentSelectedProject=projects2021
                    currYear=2021
                }
                else{
                    currentProject=1
                    totalProjects= projects2022?.projects?.size!!
                    currentSelectedProject=projects2022
                    currYear=2022
                }
                loadProject()
                projectsIndexShower!!.text=Math.min(1,totalProjects).toString()+" / "+totalProjects.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        yearsListAdapter= ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, yearsList)
        yearChooseSpinner.adapter=yearsListAdapter

        var contacts=0
        if(currentOrg.irc_channel!=null && currentOrg.irc_channel!!.isNotEmpty()){
            addContactLink(R.drawable.chat_icon,currentOrg.irc_channel!!)
            contacts++
        }
        if(currentOrg.contact_email!=null && currentOrg.contact_email!!.isNotEmpty()){
            addContactLink(R.drawable.gmail_logo_48x48,currentOrg.contact_email!!)
            contacts++
        }
        if(currentOrg.mailing_list!=null && currentOrg.mailing_list!!.isNotEmpty()){
            addContactLink(R.drawable.google_groups_logo_48x48,currentOrg.mailing_list!!)
            contacts++
        }
        if(currentOrg.twitter_url!=null && currentOrg.twitter_url!!.isNotEmpty()){
            addContactLink(R.drawable.twitter_logo_48x48,currentOrg.twitter_url!!)
            contacts++
        }
        if(currentOrg.blog_url!=null && currentOrg.blog_url!!.isNotEmpty()){
            addContactLink(R.drawable.blogger_logo_48x48,currentOrg.blog_url!!)
            contacts++
        }
        if(currentOrg.facebook_url!=null && currentOrg.facebook_url!!.isNotEmpty()){
            addContactLink(R.drawable.facebook_logo_48x48,currentOrg.facebook_url!!)
            contacts++
        }

        if(contacts==0){
            binding.contactUs.visibility=View.GONE
        }


    }

    fun addContactLink(drawable: Int, link:String){
        val one_link=layoutInflater.inflate(R.layout.one_contact_link, binding.contactUs,false)
        binding.contactUs.addView(one_link)
        val linkView=one_link.findViewById(R.id.link_to_website) as TextView
        val icon=one_link.findViewById(R.id.logo) as ImageView
        icon.setImageResource(drawable)
        linkView.text=link
        Linkify.addLinks(linkView,Linkify.ALL)

    }

    override fun onClick(view: View?) {

        if(view===binding.leftProject){
            if(currentProject!=1){
                currentProject--
                projectsIndexShower!!.text=currentProject.toString()+" / "+totalProjects.toString()
                loadProject()
            }
        }
        else if(view===binding.rightProject){
            if(currentProject!=totalProjects){
                currentProject++
                projectsIndexShower!!.text=currentProject.toString()+" / "+totalProjects.toString()
                loadProject()
            }
        }
    }
    private val map:Map<Int,String> = HashMap()
    fun loadProject(){

        if(totalProjects==0){
            binding.noCompleteProjects.visibility=View.VISIBLE
            binding.projectDescriptionLl.visibility=View.GONE
            return
        }
        binding.noCompleteProjects.visibility=View.GONE
        binding.projectDescriptionLl.visibility=View.VISIBLE

        var alteredDescription : String
        var description=currentSelectedProject!!.projects!!.get(currentProject-1).description
//        binding.noTagsDescription.text=description

        if(map.containsKey(currYear+currentProject)){
            alteredDescription= map[currYear+currentProject].toString()
        }
        else{
            description=description!!.replace("<p>","\n\n")
            description=description.replace("</p>"," ")
            description=description.replace("<code>"," ")
            description=description.replace("</code>"," ")
            description=description.replace("</code>"," ")
            description=description.replace("</a>","")
            description=description.replace("<strong>"," ")
            description=description.replace("</strong>","")
            description=description.replace("<em>","")
            description=description.replace("</em>","")
            description=description.replace("<br>","\n")
            description=description.replace("<li>","")
            description=description.replace("</li>","\n")
            description=description.replace("<ul>","")
            description=description.replace("</ul>","")
            description=description.replace("<ol>","")
            description=description.replace("</ol>","")

            // removing all heading tags
            description=description.replace("<h1>","")
            description=description.replace("</h1>","")
            description=description.replace("<h2>","")
            description=description.replace("</h2>","")
            description=description.replace("<h3>","")
            description=description.replace("</h3>","")
            description=description.replace("<h4>","")
            description=description.replace("</h4>","")
            description=description.replace("<h5>","")
            description=description.replace("</h5>","")
            description=description.replace("<h6>","")
            description=description.replace("</h6>","")

            // removing all href links
            while(description!!.contains("<a href")){
                val ind=description.indexOf("<a href")
                val end=description.indexOf(">",ind)
                description=description.substring(0,ind)+description.substring(end+1)
            }
            while(description!!.contains("\n\n\n")){
                description=description.replace("\n\n\n","\n\n")
            }
            alteredDescription=description
        }

        binding.projectTitle.text=currentSelectedProject!!.projects!!.get(currentProject-1).title
        binding.projectDescription.text=alteredDescription
        binding.projectStudent.text="Student name : "+currentSelectedProject!!.projects!!.get(currentProject-1).studentName
        projectLink=currentSelectedProject!!.projects!!.get(currentProject-1).projectUrl!!
        projectSourceLink=currentSelectedProject!!.projects!!.get(currentProject-1).codeUrl!!
    }
}