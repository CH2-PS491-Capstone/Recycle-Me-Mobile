package com.bangkit.recycleme.ui.article

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.ui.detail.DetailActivity
import com.bangkit.recycleme.adapter.ListPhoneAdapter
import com.bangkit.recycleme.models.Phone
import com.bangkit.recycleme.R

class ArticleFragment : Fragment() {
    private lateinit var rvPhone: RecyclerView
    private val list = ArrayList<Phone>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)

        rvPhone = view.findViewById(R.id.rv_phone)
        rvPhone.setHasFixedSize(true)

        list.addAll(getListPhone())

        showRecyclerList()

        setHasOptionsMenu(true)

        return view
    }

    private fun getListPhone(): ArrayList<Phone> {
        val dataName = resources.getStringArray(R.array.data_name)
        val maxDescriptionLength = 25
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val dataPrice = resources.getStringArray(R.array.data_price)
        val detailDescription = resources.getStringArray(R.array.data_description)
        val listPhone = ArrayList<Phone>()
        for (i in dataName.indices) {
            val description = if (dataDescription[i].length > maxDescriptionLength) {
                val truncatedDescription = dataDescription[i].substring(0, maxDescriptionLength)
                "$truncatedDescription... Read More"
            } else {
                dataDescription[i]
            }
            val phone = Phone(dataName[i], description, detailDescription[i], dataPhoto.getResourceId(i, -1), dataPrice[i])
            listPhone.add(phone)
        }

        return listPhone
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(requireContext())
        rvPhone.layoutManager = layoutManager
        val listPhoneAdapter = ListPhoneAdapter(list)
        rvPhone.adapter = listPhoneAdapter

        listPhoneAdapter.setOnItemClickCallback(object : ListPhoneAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Phone) {
                val intentToDetail = Intent(requireContext(), DetailActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                startActivity(intentToDetail)
            }
        })
    }
}
