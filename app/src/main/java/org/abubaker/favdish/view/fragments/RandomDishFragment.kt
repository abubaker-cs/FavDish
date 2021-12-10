package org.abubaker.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.abubaker.favdish.databinding.FragmentRandomDishBinding

class RandomDishFragment : Fragment() {
    
    private var binding: FragmentRandomDishBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize the ViewBinding.
        binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    // Override the onDestroy method and make the ViewBinding null when it is called.
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}