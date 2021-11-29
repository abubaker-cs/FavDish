package org.abubaker.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.abubaker.favdish.databinding.FragmentRandomDishBinding
import org.abubaker.favdish.viewModel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private lateinit var randomDishViewModel: RandomDishViewModel
    private var _binding: FragmentRandomDishBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        randomDishViewModel =
            ViewModelProvider(this)[RandomDishViewModel::class.java]

        _binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications

        randomDishViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}