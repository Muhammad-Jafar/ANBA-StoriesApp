package jafar.stories.features.main.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jafar.stories.data.model.ListStory
import jafar.stories.databinding.FragmentDetailMarkPointBinding
import jafar.stories.utils.Constanta
import jafar.stories.utils.getUploadStoryTime
import jafar.stories.utils.parseAddressLocation

class DetailMarkPointFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailMarkPointBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMarkPointBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments != null) {
            arguments?.apply {
                val data = this.getParcelable<ListStory>(Constanta.EXTRA_DATA)
                val address = parseAddressLocation(
                    requireContext(), data?.lat?.toDouble()!!, data.lon?.toDouble()!!
                )
                binding.apply {
                    Glide.with(binding.root).load(getString(data.photoUrl)).into(markPointImage)
                    markPointName.text = data.name
                    markPointDesc.text = data.description
                    markPointCreatedAt.text = getUploadStoryTime(data.createdAt)
                    markPointAddress.text = address
                }
            }
        } else Toast.makeText(requireContext(), "No Data", Toast.LENGTH_SHORT).show()
    }
}