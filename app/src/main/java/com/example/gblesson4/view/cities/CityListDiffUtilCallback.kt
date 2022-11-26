package com.example.gblesson4.view.cities

import androidx.recyclerview.widget.DiffUtil
import com.example.gblesson4.model.Weather

class CitiesListDiffUtilCallback(private val oldList: List<Weather>, private val newList: List<Weather>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].city.lat == newList[newItemPosition].city.lat &&
                oldList[oldItemPosition].city.lon == newList[newItemPosition].city.lon
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].city.lat == newList[newItemPosition].city.lat &&
                oldList[oldItemPosition].city.lon == newList[newItemPosition].city.lon &&
                (oldList[oldItemPosition].temperature == newList[newItemPosition].temperature ||
                        oldList[oldItemPosition].feelsLike == newList[newItemPosition].feelsLike)
    }
}