package com.dentify.dentify.ui.fragments.profile


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.dentify.dentify.R
import com.dentify.dentify.apiModel.model.Language


class LanguageAdapter(context: Context, countries: ArrayList<Language>) : ArrayAdapter<Language>(context, 0, countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
        val country = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )

        country?.let {
            view.findViewById<TextView>(R.id.country).text = country.countryName
        }
        return view
    }
}