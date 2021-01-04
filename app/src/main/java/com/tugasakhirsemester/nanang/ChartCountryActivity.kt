package com.tugasakhirsemester.nanang

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.tugasakhirsemester.nanang.network.InfoCountry
import com.tugasakhirsemester.nanang.network.InfoService
import kotlinx.android.synthetic.main.activity_chart_country.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class ChartCountryActivity : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences
    private var sharedPrefFile = "kotlinsharedpreferences"
    private var dayCases = ArrayList<String>()

    companion object{
        const val EXTRA_COUNTRY = "country"
        const val EXTRA_DATE = "date"
        const val EXTRA_COUNTRY_CODE = "country_code"

        const val EXTRA_NEW_DEATH = "new_death"
        const val EXTRA_NEW_CONFIRMED = "new_confirmed"
        const val EXTRA_NEW_RECOVERED = "new_recovered"

        const val EXTRA_TOTAL_CONFIRMED = "total_confirmed"
        const val EXTRA_TOTAL_DEATH = "total_death"
        const val EXTRA_TOTAL_RECOVERED = "total_recovered"

        lateinit var dataCountry: String
        lateinit var dataFlag: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_country)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val country = intent.getStringExtra(EXTRA_COUNTRY)
        val date = intent.getStringExtra(EXTRA_DATE)
        val countryCode = intent.getStringExtra(EXTRA_COUNTRY_CODE)

        val newConfirmed = intent.getStringExtra(EXTRA_NEW_CONFIRMED)
        val newDeath = intent.getStringExtra(EXTRA_NEW_DEATH)
        val newRecovered = intent.getStringExtra(EXTRA_NEW_CONFIRMED)

        val totalConfirmed = intent.getStringExtra(EXTRA_TOTAL_CONFIRMED)
        val totalDeath = intent.getStringExtra(EXTRA_TOTAL_DEATH)
        val totalRecovered = intent.getStringExtra(EXTRA_TOTAL_RECOVERED)

        txt_country_chart.text = country
        txt_current.text = date

        txt_total_confirmed_current.text = totalConfirmed.toString()
        txt_total_deaths_current.text = totalDeath.toString()
        txt_total_recovered_currentl.text = totalRecovered.toString()

        txt_new_confirmed_current.text = newConfirmed.toString()
        txt_new_deaths_current.text = newDeath.toString()
        txt_new_recovered_current.text = newRecovered.toString()

        val formatter: NumberFormat = DecimalFormat("#,###")
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(country, country)
        editor.apply() // untuk menyimoan data yang telah ditaruh
        editor.commit() // untuk menerapkan sharedPreferences

        val saveDataCountry = sharedPreferences.getString(country, country) // untuk menampung data yang telah tersimpan ke dalam variabel saveDataCountry
        val saveCountryFlag = sharedPreferences.getString(countryCode, countryCode)
        dataCountry = saveDataCountry.toString() // untuk convert data to string
        dataFlag = saveCountryFlag.toString() + "/flat/64.png"

        if(saveCountryFlag != null) {
            Glide.with(this).load("https://www.countryflags.io/$dataFlag")
                .into(img_flag_chart)
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
        }

        getCountry()


    }
    private fun getCountry() {
        val okHttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/dayone/country/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(InfoService::class.java)
        api.getInfoService(dataCountry).enqueue(object :Callback<List<InfoCountry>>{
            override fun onFailure(call: Call<List<InfoCountry>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<InfoCountry>>, response: Response<List<InfoCountry>>) {
                if (response.isSuccessful) {
                    val dataCovid = response.body()
                    val barEntries1: ArrayList<BarEntry> = ArrayList()
                    val barEntries2: ArrayList<BarEntry> = ArrayList()
                    val barEntries3: ArrayList<BarEntry> = ArrayList()
                    val barEntries4: ArrayList<BarEntry> = ArrayList()
                    var i = 0

                    while (i < dataCovid?.size ?: 0) {
                        for (s in dataCovid!!) {
                            val barEntry1 = BarEntry(i.toFloat(), s.Confirmed.toFloat())
                            val barEntry2 = BarEntry(i.toFloat(), s.Deaths.toFloat())
                            val barEntry3 = BarEntry(i.toFloat(), s.Recovered.toFloat())
                            val barEntry4 = BarEntry(i.toFloat(), s.Active.toFloat())

                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
                            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
                            val date: Date? = inputFormat.parse(s.Date)
                            val formattedDate: String = outputFormat.format(date!!)
                            dayCases.add(formattedDate)

                            barEntries1.add(barEntry1)
                            barEntries2.add(barEntry2)
                            barEntries3.add(barEntry3)
                            barEntries4.add(barEntry4)

                            i++
                        }
                    }

                    val xAxis: XAxis = barChartView.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(dayCases)
                    barChartView.axisLeft.axisMinimum = 0f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.setCenterAxisLabels(true)
                    xAxis.isGranularityEnabled = true

                    val barDataSet1 = BarDataSet(barEntries1, "Confirmed")
                    val barDataSet2 = BarDataSet(barEntries2, "Deaths")
                    val barDataSet3 = BarDataSet(barEntries3, "Recovered")
                    val barDataSet4 = BarDataSet(barEntries4, "Active")

                    barDataSet1.setColors(Color.parseColor("#FF5722"))
                    barDataSet2.setColors(Color.parseColor("#FFEB3B"))
                    barDataSet3.setColors(Color.parseColor("#84FFFF"))
                    barDataSet4.setColors(Color.parseColor("#FF448AFF"))

                    val data = BarData(barDataSet1, barDataSet2, barDataSet3, barDataSet4)
                    barChartView.data = data

                    val barSpace = 0.02f
                    val groupSpace = 0.3f
                    val groupCount = 4f

                    data.barWidth = 0.15f
                    barChartView.invalidate()
                    barChartView.setNoDataTextColor(android.R.color.black)
                    barChartView.setTouchEnabled(true)
                    barChartView.description.isEnabled = false

                    // TODO AXIS MINIMUM
                    barChartView.xAxis.axisMinimum = 0f
                    barChartView.setVisibleXRangeMaximum(0f + barChartView.barData.getGroupWidth(groupSpace, barSpace) * groupCount)
                    barChartView.groupBars(0f, groupSpace, barSpace)
                }

                /*if (response.isSuccessful) {
                    val dataCovid = response.body()
                    val barEntries1: ArrayList<BarEntry> = ArrayList()
                    val barEntries2: ArrayList<BarEntry> = ArrayList()
                    val barEntries3: ArrayList<BarEntry> = ArrayList()
                    val barEntries4: ArrayList<BarEntry> = ArrayList()
                    var i = 0

                    while (i < dataCovid?.size ?: 0) {
                        for (s in dataCovid!!) {
                            val barEntry1 = BarEntry(i.toFloat(), s.Confirmed.toFloat())
                            val barEntry2 = BarEntry(i.toFloat(), s.Deaths.toFloat())
                            val barEntry3 = BarEntry(i.toFloat(), s.Recovered.toFloat())
                            val barEntry4 = BarEntry(i.toFloat(), s.Active.toFloat())

                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
                            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
                            val date: Date? = inputFormat.parse(s.Date)
                            val formattedDate: String = outputFormat.format(date!!)
                            dayCases.add(formattedDate)

                            barEntries1.add(barEntry1)
                            barEntries2.add(barEntry2)
                            barEntries3.add(barEntry3)
                            barEntries4.add(barEntry4)

                            i++

                        }

                    }

                    val xAxis: XAxis = barChartView.xAxis
                    barChartView.axisLeft.axisMinimum = 0f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.setCenterAxisLabels(true)
                    xAxis.isGranularityEnabled = true
                    xAxis.valueFormatter = IndexAxisValueFormatter(dayCases)

                    val barDataSet1 = BarDataSet(barEntries1, "Confirmed")
                    val barDataSet2 = BarDataSet(barEntries1, "Deaths")
                    val barDataSet3 = BarDataSet(barEntries1, "Recovered")
                    val barDataSet4 = BarDataSet(barEntries1, "Active")
                    barDataSet1.setColors(Color.parseColor("#F44336"))
                    barDataSet2.setColors(Color.parseColor("#FFEB3B"))
                    barDataSet3.setColors(Color.parseColor("#03DAC5"))
                    barDataSet4.setColors(Color.parseColor("#2196F3"))

                    val data = BarData(barDataSet1, barDataSet2, barDataSet3, barDataSet4)
                    barChartView.data = data
//                    barChartView.xAxis.axisMinimum = 0f
//                    xAxis.position = XAxis.XAxisPosition.BOTTOM
//                    xAxis.granularity = 1f
//                    xAxis.setCenterAxisLabels(true)
//                    xAxis.isGranularityEnabled = true

                    val barSpace = 0.02f
                    val groupSpace = 0.3f
                    val groupCount = 4f

                    data.barWidth = 0.15f
                    barChartView.xAxis.axisMinimum = 0f
                    barChartView.invalidate()
                    barChartView.setNoDataTextColor(R.color.black)
                    barChartView.setTouchEnabled(true)
                    barChartView.description.isEnabled = false

                    barChartView.setVisibleXRangeMaximum(0f + barChartView.barData.getGroupWidth(groupSpace, barSpace) * groupCount)
                    barChartView.groupBars(0f, groupSpace, barSpace)


                }*/
            }
        })
    }

}