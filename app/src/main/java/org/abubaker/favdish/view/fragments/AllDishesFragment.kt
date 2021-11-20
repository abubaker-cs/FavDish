package org.abubaker.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.FragmentAllDishesBinding
import org.abubaker.favdish.view.activities.AddUpdateDishActivity
import org.abubaker.favdish.viewModel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentAllDishesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable menu in the ActionBar
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * XML: menu_all_dishes
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // Inflate: menu_all_dishes.xml
        inflater.inflate(R.menu.menu_all_dishes, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            // Add Dish
            R.id.action_add_dish -> {
                // Note: Inside the Fragment we cannot say "this", because the Fragment
                // does not have a "context" by itself. So, we need to ge the "context" of the
                // Activity in which it exists by using requireActivity() + Where we want to goto
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}