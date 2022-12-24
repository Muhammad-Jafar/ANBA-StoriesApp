package jafar.stories.features.main.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import jafar.stories.R
import jafar.stories.data.model.ListStory
import jafar.stories.databinding.ActivityDetailStoryBinding
import jafar.stories.utils.Constanta
import jafar.stories.utils.formatDate
import jafar.stories.utils.parseAddressLocation
import java.util.*

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDetailStory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f

        val data = intent.extras?.getParcelable<ListStory>(Constanta.EXTRA_DATA)
        val address =
            if (data?.lat == null && data?.lon == null) resources.getString(R.string.location_null)
            else parseAddressLocation(this, data.lat!!.toDouble(), data.lon!!.toDouble())

        binding.apply {
            Glide.with(applicationContext).load(data?.photoUrl).into(ivDetailPhoto)
            tvDetailName.text = data?.name
            tvDetailDescription.text = data?.description
            tvTimeUploaded.text = formatDate(data?.createdAt!!, TimeZone.getDefault().id)
            tvDetailAddress.text = address
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
