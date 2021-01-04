package com.tugasakhirsemester.nanang.network

data class AllCountries(
    val Global: World,
    val Countries: List<Countries>

)

data class World(
    val TotalConfirmed: String = "",
    val TotalRecovered: String = "",
    val TotalDeaths: String = ""

)

data class Countries(
    val Country:String = "",
    val Date:String = "",
    val NewConfirmed:String = "",
    val TotalConfirmed:String = "",
    val TotalDeaths:String = "",
    val NewDeaths:String = "",
    val TotalRecovered:String = "",
    val NewRecovered:String = "",
    val CountryCode:String = ""

)

data class InfoCountry(
    val Deaths:String = "",
    val Confirmed:String = "",
    val Recovered:String = "",
    val Active:String = "",
    val Date:String = ""

)