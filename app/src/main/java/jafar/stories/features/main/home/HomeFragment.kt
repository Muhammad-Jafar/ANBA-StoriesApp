package jafar.stories.features.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import jafar.stories.R
import jafar.stories.databinding.FragmentHomeBinding
import jafar.stories.features.main.HomeViewModel
import jafar.stories.features.main.about.AboutActivity
import jafar.stories.features.main.addstory.FromAddStoryActivity
import jafar.stories.utils.ViewModelFactory
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel
    private val listAdapter = StoryItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.option_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                (R.id.logoutButton) ->
                    LogoutFragment()
                        .show(childFragmentManager, LogoutFragment::class.java.simpleName)
                (R.id.aboutButton) ->
                    startActivity(Intent(requireActivity(), AboutActivity::class.java))
            }
            true
        }

        binding.listStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            isNestedScrollingEnabled = false
            adapter =
                listAdapter.withLoadStateFooter(footer = StoryItemLoadingAdapter { listAdapter.retry() })

            setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                binding.apply {
                    if (scrollY > oldScrollY && addStoryButton.isExtended) addStoryButton.shrink()
                    if (scrollY < oldScrollY && !addStoryButton.isExtended) addStoryButton.extend()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModel()
        binding.apply {
            (viewModel as HomeViewModel).getStories.observe(viewLifecycleOwner) {
                listAdapter.submitData(lifecycle, it)
            }
            swipeRefresh.setOnRefreshListener { onRefresh() }
            addStoryButton.setOnClickListener {
                startActivity(Intent(requireActivity(), FromAddStoryActivity::class.java))
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getStoryInstance(requireContext())
        val viewModel: HomeViewModel by viewModels { factory }
        this.viewModel = viewModel
    }

    override fun onRefresh() {
        binding.apply {
            swipeRefresh.isRefreshing = true
            listAdapter.refresh()
            Timer().schedule(2000) {
                swipeRefresh.isRefreshing = false
                listStory.smoothScrollToPosition(0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}