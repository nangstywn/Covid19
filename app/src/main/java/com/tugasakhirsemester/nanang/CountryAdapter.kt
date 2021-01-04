package com.tugasakhirsemester.nanang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugasakhirsemester.nanang.network.Countries
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CountryAdapter(val country: ArrayList<Countries>, val clickListener: (Countries) -> Unit) :
    RecyclerView.Adapter<CountryAdapter.CountryViewHolder>(), Filterable{

    var countryFilterList = ArrayList<Countries>()
    init {
        countryFilterList = country
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_country, parent, false))


    override fun getItemCount() = countryFilterList.size

    override fun onBindViewHolder(holder: CountryAdapter.CountryViewHolder, position: Int) {
        holder.bindItem(countryFilterList[position], clickListener)
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charSearch = constraint.toString()
                countryFilterList = if (charSearch.isEmpty()){
                    country
                } else {
                    var resultList = ArrayList<Countries>()
                    for (row in country){
                        if (row.Country.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))
                        ){
                            resultList.add(row)
                        }
                    }
                    resultList
                }

                val filterResult = FilterResults()
                filterResult.values = countryFilterList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<Countries>
                notifyDataSetChanged()
            }
        }
    }

    class CountryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val tvCountry = itemView.findViewById<TextView>(R.id.txt_country_name)
        val tvTotalCase = itemView.findViewById<TextView>(R.id.txt_total_case)
        val tvTotalRecovered = itemView.findViewById<TextView>(R.id.txt_total_recovered)
        val tvTotalDeaths = itemView.findViewById<TextView>(R.id.txt_total_deaths)
        val imgFlag = itemView.findViewById<ImageView>(R.id.img_flag_country)

        fun bindItem(countries: Countries, clickListener: (Countries) -> Unit){
            tvCountry.text = countries.Country

            val formatter: NumberFormat = DecimalFormat("#,###")
            tvTotalCase.text = formatter.format(countries.TotalConfirmed.toDouble())
            tvTotalRecovered.text = formatter.format(countries.TotalRecovered.toDouble())
            tvTotalDeaths.text = formatter.format(countries.TotalDeaths.toDouble())
            itemView.setOnClickListener{ clickListener(countries) }

            Glide.with(itemView.context)
                .load("http://www.countryflags.io/" + countries.CountryCode + "/flat/64.png")
                .into(imgFlag)
            itemView.setOnClickListener { clickListener(countries) }
        }

    }
}