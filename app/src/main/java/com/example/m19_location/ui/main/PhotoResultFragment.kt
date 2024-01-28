package com.example.m19_location.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.m19_location.R
import com.example.m19_location.data.App
import com.example.m19_location.data.SinglePhoto
import com.example.m19_location.databinding.FragmentPhotoResultBinding
import kotlinx.coroutines.launch


class PhotoResultFragment : Fragment() {

    private var _binding: FragmentPhotoResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var allPhotosCollected: List<SinglePhoto>

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val photoDao = (activity?.applicationContext as App).db.photoDao()
                return MainViewModel(photoDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultState.collect {
                    allPhotosCollected = it
                    binding.recyclerView.layoutManager =
                        GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)
                    val myAdapter = allPhotosCollected?.let { it1 -> PhotosAdapter(it1) }
                    binding.recyclerView.adapter = myAdapter

                }
            }
        }

        binding.button2.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, MainFragment())
                ?.commit();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}