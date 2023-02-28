package com.myapps.gsocdata.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.indices
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.myapps.gsocdata.R
import com.myapps.gsocdata.adapters.OrgsListAdapter
import com.myapps.gsocdata.databinding.ActivityMainBinding
import com.myapps.gsocdata.models.ApiUtilities
import com.myapps.gsocdata.models.OneOrg
import com.myapps.gsocdata.models.OrgList2023
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var binding : ActivityMainBinding

    lateinit var progressDialog : ProgressDialog

    lateinit var org_list : RecyclerView
    lateinit var list_adapter : OrgsListAdapter
    lateinit var list_of_org : ArrayList<OneOrg>

    var list_of_org_actual : ArrayList<OneOrg> = ArrayList()
    val listOfTotalTechStackList : ArrayList<StringAndInt> = ArrayList()
    val listOfTotalTechStackSet : HashMap<String,Int> = HashMap()

    lateinit var filterDialog : Dialog
    var lastSelectedTech : TextView?=null
    val listOfSelectedTechStack=ArrayList<TextView>()
    var onlySelectedYears=0;
    val isSelected = arrayOf(0,0,0,0,0,0,0,0)
    var selYearCount=0

    val orgsIn2023=HashSet<String>()


    var totalProjectsMin=0
    var totalProjectsMax=300
    var averageProjectsMin=0
    var averageProjectsMax=300


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("tyho","created")

        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Fetching GSOC info")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        filterDialog= Dialog(this)

        binding.editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("knm","called")
                filterOrgNames(p0!!)
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })

        list_of_org=ArrayList()
        org_list=binding.orgRecyclerView
        org_list.layoutManager=LinearLayoutManager(this)
        list_adapter=OrgsListAdapter(list_of_org,this@MainActivity)
        org_list.adapter=list_adapter


        ApiUtilities.apiInterface.getFullOrglist()?.enqueue(object : Callback<ArrayList<OneOrg>> {
            override fun onResponse(call: Call<ArrayList<OneOrg>>, response: Response<ArrayList<OneOrg>>) {

                for(i in response.body()!!.indices){
                    response.body()!![i].fillYearsData()
                }
                list_of_org.addAll(response.body()!!)

                list_of_org_actual.addAll(response.body()!!)
                list_adapter.notifyDataSetChanged()

                fillTechStackInList()
                createFilterDialog()
                filterOrgNames(binding.editTextTextPersonName.text)
                binding.filterButton.setOnClickListener(this@MainActivity)

                closeProgressDialog()

            }

            override fun onFailure(call: Call<ArrayList<OneOrg>>, t: Throwable) {
                Log.i("lo",t.toString())
            }

        })

        ApiUtilities.apiInterface.get2023Orglist()?.enqueue(object : Callback<OrgList2023> {
            override fun onResponse(call: Call<OrgList2023>, response: Response<OrgList2023>) {

                for(i in response.body()!!.organizations!!.indices){
//                    response.body()!!.organizations?.get(i)?.fillYearsData()
                    orgsIn2023.add(response.body()!!.organizations?.get(i)!!.name!!)
                }
                list_adapter.add2023Orgs(orgsIn2023)
                closeProgressDialog()
            }

            override fun onFailure(call: Call<OrgList2023>, t: Throwable) {
                Log.i("lo",t.toString())
            }

        })
    }

    // this method filters org names according to the filters user has given
    // and the name user has typed in the search bar
    fun filterOrgNames(p0 : CharSequence){
        val input=p0.toString()
        list_adapter.mList.clear()
        list_adapter.notifyDataSetChanged()

        Log.i("levls","lvl 2")

        var t=0
        for(i in list_of_org_actual.indices){

            // checking years in GSOC for org
            var addable=true
            if(selYearCount>0){
                for(j in isSelected.indices){
                    if(isSelected[j]==1){
                        if(j==7){
                            addable=addable && orgsIn2023.contains(list_of_org_actual[i].name.toString())
                        }
                        else{
                            addable=addable && list_of_org_actual[i].years!!.yearsParticipateIn.contains((2016+j).toString())
                        }
                    }
                    else if(onlySelectedYears==1){
                        if(j==7){
                            addable=addable && !orgsIn2023.contains(list_of_org_actual[i].name.toString())
                        }
                        else{
                            addable=addable && !list_of_org_actual[i].years!!.yearsParticipateIn.contains((2016+j).toString())

                        }
                    }
                }
            }


            // checking tech stack match
            var addable2=true
            for(j in listOfSelectedTechStack.indices){
                addable2=addable2 && list_of_org_actual[i].technologies!!.contains(listOfSelectedTechStack[j].text)
            }

            //checking max projects match
            val tot=list_of_org_actual[i].years!!.totalProjects
            val addable3 = tot>=totalProjectsMin && tot<=totalProjectsMax

            //checking average projects match
            val totAvg=list_of_org_actual[i].years!!.avgProjects
            val addable4 = totAvg>=averageProjectsMin && totAvg<=averageProjectsMax


            if(list_of_org_actual[i].name!!.lowercase().contains(input.lowercase())){
                if(addable && addable2 && addable3 && addable4){
                    t++
                    list_adapter.mList.add(list_of_org_actual[i])
                    list_adapter.notifyItemInserted(list_adapter.mList.size-1)
                }
            }

        }

        Log.i("levls","lvl 3")

        binding.textView.text="Total "+t+" results shown"
    }

    fun fillTechStackInList(){
        for(i in list_of_org_actual.indices){
            val techList=list_of_org_actual[i].technologies
            for(j in techList!!.indices){
                val temp=techList[j].lowercase()
                if(!listOfTotalTechStackSet.contains(temp)){
                    listOfTotalTechStackSet[temp] = 1
                }
                else{
                    listOfTotalTechStackSet[temp] = listOfTotalTechStackSet[temp]!! + 1
                }
            }
        }
        val it=listOfTotalTechStackSet.keys.iterator()
        while(it.hasNext()){
            val st=it.next()
            listOfTotalTechStackList.add(StringAndInt(st, listOfTotalTechStackSet[st]!!))
        }
        listOfTotalTechStackList.sortWith{ a, b ->
            when {
                (a.num>b.num) -> -1
                (a.num<b.num) -> 1
                else -> 0
            }
        }
    }


    override fun onClick(view: View?) {
        if(view===binding.filterButton){
            filterDialog.show()
        }
    }

    private fun createFilterDialog(){
        filterDialog.setContentView(R.layout.filters_dialog_box)
        val window: Window = filterDialog.window!!
//        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val switch=filterDialog.findViewById<SwitchCompat>(R.id.year_select_switch)
        switch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                onlySelectedYears = if(p1) 1 else 0
                filterOrgNames(binding.editTextTextPersonName.text.toString())
            }

        })
        val yearL = arrayOf("2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023")
        var yearSelectOptions=filterDialog.findViewById<FlexboxLayout>(R.id.year_select_filter)
        for(i in yearL.indices){
            val oneYear=layoutInflater.inflate(R.layout.one_topic_or_tech_stack,yearSelectOptions,false) as TextView
            oneYear.text=yearL[i]
            oneYear.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            oneYear.setOnClickListener{view ->
                val pos=(view as TextView).text.toString().toInt()-2016
                if(isSelected[pos]==0){
                    selYearCount++
                    isSelected[pos]=1
                    view.setBackgroundResource(R.drawable.solid_blue_curved_border)
                }
                else{
                    selYearCount--
                    isSelected[pos]=0
                    view.setBackgroundResource(R.drawable.solid_light_blue_curved_border)
                }
                filterOrgNames(binding.editTextTextPersonName.text)
            }
            yearSelectOptions.addView(oneYear)
        }



        var techList=filterDialog.findViewById<FlexboxLayout>(R.id.tech_stack_filters)
        val list_of_tech_stach=listOfTotalTechStackList
        for(i in list_of_tech_stach.indices){
            val techLl=layoutInflater.inflate(R.layout.one_tech_filter,techList,false) as LinearLayout
            val techName=techLl.findViewById<TextView>(R.id.tech_name)
            val techCount=techLl.findViewById<TextView>(R.id.tech_count)
            techName.text=listOfTotalTechStackList[i].str
            techCount.text=listOfTotalTechStackList[i].num.toString()
            techName.setOnClickListener{view ->
                val tv=view as TextView
                if(listOfSelectedTechStack.size==2){
                    if(listOfSelectedTechStack.contains(tv)){
                        (tv.parent as LinearLayout).setBackgroundResource(R.drawable.solid_light_blue_curved_border)
                        listOfSelectedTechStack.remove(tv)
                        lastSelectedTech=listOfSelectedTechStack[0]
                    }
                    else{
                        (lastSelectedTech!!.parent as LinearLayout).setBackgroundResource(R.drawable.solid_light_blue_curved_border)
                        listOfSelectedTechStack.remove(lastSelectedTech)
                        lastSelectedTech=listOfSelectedTechStack[0]
                        (tv.parent as LinearLayout).setBackgroundResource(R.drawable.solid_blue_curved_border)
                        listOfSelectedTechStack.add(tv)
                    }
                }
                else if(listOfSelectedTechStack.size==0){
                    lastSelectedTech=tv
                    listOfSelectedTechStack.add(tv)
                    (tv.parent as LinearLayout).setBackgroundResource(R.drawable.solid_blue_curved_border)
                }
                else{
                    if(listOfSelectedTechStack.contains(tv)){
                        listOfSelectedTechStack.remove(tv)
                        (tv.parent as LinearLayout).setBackgroundResource(R.drawable.solid_light_blue_curved_border)
                        lastSelectedTech=null
                    }
                    else{
                        listOfSelectedTechStack.add(tv)
                        (tv.parent as LinearLayout).setBackgroundResource(R.drawable.solid_blue_curved_border)
                    }
                }
                val tCShower=filterDialog.findViewById<LinearLayout>(R.id.added_tech_stack_shower)
                while(tCShower.childCount!=0){
                    tCShower.removeViewAt(0)
                }
                for(j in listOfSelectedTechStack.indices){
                    val tv=layoutInflater.inflate(R.layout.one_topic_or_tech_stack,tCShower,false) as TextView
                    tv.setBackgroundResource(R.drawable.solid_blue_curved_border)
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
                    tv.text=listOfSelectedTechStack[j].text
                    tCShower.addView(tv)
                }
                filterOrgNames(binding.editTextTextPersonName.text)
            }
            techList.addView(techLl)
        }

        var searchTechStack=filterDialog.findViewById<EditText>(R.id.tech_stack_name_search)
        searchTechStack.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("knm","called")
//                filterTechStackNames(p0!!)
                for(i in techList.indices){
                    val tv=(techList.getChildAt(i) as LinearLayout).getChildAt(0) as TextView
                    if(tv.text.contains(searchTechStack.text.toString())){
                        (tv.parent as LinearLayout).visibility=View.VISIBLE
                    }
                    else{
                        (tv.parent as LinearLayout).visibility=View.GONE
                    }
                }
            }


            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })

        val minTotalProjects=filterDialog.findViewById<EditText>(R.id.min_total_projects)
        minTotalProjects.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().length<5){
                    if(p0.toString().isNotEmpty()){
                        totalProjectsMin=p0.toString().toInt()
                    }
                    else{
                        totalProjectsMin=0
                    }
                }
                filterOrgNames(binding.editTextTextPersonName.text)
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })
        val maxTotalProjects=filterDialog.findViewById<EditText>(R.id.max_total_projects)
        maxTotalProjects.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().length<5){
                    totalProjectsMax = if(p0.toString().isNotEmpty()) p0.toString().toInt() else 500
                }
                filterOrgNames(binding.editTextTextPersonName.text)
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })


        val minAverageProjects=filterDialog.findViewById<EditText>(R.id.min_average_projects)
        minAverageProjects.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().length<5){
                    averageProjectsMin = if(p0.toString().isNotEmpty()) p0.toString().toInt() else 0
                }
                filterOrgNames(binding.editTextTextPersonName.text)
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })
        val maxAverageProjects=filterDialog.findViewById<EditText>(R.id.max_average_projects)
        maxAverageProjects.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().length<5){
                    averageProjectsMax = if(p0.toString().isNotEmpty()) p0.toString().toInt() else 100
                }
                filterOrgNames(binding.editTextTextPersonName.text)
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })
    }

    var times=0
    fun closeProgressDialog(){
        if(++times==2){
            progressDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        filterDialog.dismiss()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//            val window: Window = filterDialog.window!!
//            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 200)
//        }
//        else{
//            val window: Window = filterDialog.window!!
//            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        }
    }

}

class StringAndInt(var str:String,var num:Int)