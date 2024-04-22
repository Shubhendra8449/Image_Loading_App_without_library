package com.imageloadingapp.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imageloadingapp.presentation.adapter.ImageLoadAdapter
import com.imageloadingapp.presentation.viewmodel.ImageVM
import com.imageloadingapp.data.remote.base.Status
import com.imageloadingapp.databinding.ActivityMainBinding
import com.imageloadingapp.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val vm by viewModels<ImageVM>()
    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var adapter: ImageLoadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        callGetImageApi()
        setAdapter()
        observerResponse()


    }

    private fun callGetImageApi() {
        vm.fetchImages()
    }

    private fun setAdapter() {
        recyclerView = mainBinding.imageRv
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
    }

    private fun observerResponse() {
        //observing showAge API Response
        vm.imagesResponseMLD.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { it1 -> adapter.setImage(it1) }
                    mainBinding.progressBar.visibility = View.INVISIBLE

                }

                Status.ERROR -> {
                    mainBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this,
                        AppUtils.apiError(it.errorModel?.message.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Status.LOADING -> {

                }
            }
        }
    }
}