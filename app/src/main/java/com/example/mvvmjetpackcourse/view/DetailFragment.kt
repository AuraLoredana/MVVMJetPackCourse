package com.example.mvvmjetpackcourse.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mvvmjetpackcourse.R
import com.example.mvvmjetpackcourse.databinding.FragmentDetailBinding
import com.example.mvvmjetpackcourse.databinding.SendSmsDialogBinding
import com.example.mvvmjetpackcourse.model.DogBreed
import com.example.mvvmjetpackcourse.model.DogPalette
import com.example.mvvmjetpackcourse.model.SmsInfo
import com.example.mvvmjetpackcourse.viewModel.DetailViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private var dogUuid = 0
    private lateinit var dataBinding: FragmentDetailBinding
    private var sendSMSStarted = false
    private var currentDog: DogBreed? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogBreed.observe(this, androidx.lifecycle.Observer { dog ->
            dog?.let {
                currentDog = dog
                dataBinding.dog = dog
                it.imageUrl?.let { imgColor ->
                    setBackgroundColor(imgColor)
                }

//                dogName.text = dog.dogBreed
//                dogLifespan.text = dog.lifeSpan
//                dogTemperament.text = dog.temperament
//                dogPurpose.text = dog.bredFor
//                getProgressDrawable(context)?.let { it1 -> dogImage.loadImage(dog.imageUrl, it1) }
            }
        })
    }

    private fun setBackgroundColor(url: String) {
        Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Palette.from(resource).generate { palette ->
                    val intColor = palette?.lightVibrantSwatch?.rgb ?: 0
                    val myPalette = DogPalette(intColor)
                    dataBinding.palette = myPalette
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                sendSMSStarted = true
                (activity as MainActivity).checkSmsPermission()
            }

            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Dogs")
                intent.putExtra(Intent.EXTRA_STREAM, "${currentDog?.imageUrl}")
                intent.putExtra(Intent.EXTRA_TEXT, "Dog app")
                startActivity(Intent.createChooser(intent, "Share"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSMSStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo("", "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}", currentDog?.imageUrl)

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it), R.layout.send_sms_dialog, null, false
                )

                AlertDialog.Builder(it).setView(dialogBinding.root).setPositiveButton("Send SMS") { dialog, which ->
                    if (!dialogBinding.smsInfo?.text.isNullOrEmpty()) {
                        smsInfo.to = dialogBinding.smsInfo?.text.toString()
                        sendSms(smsInfo)
                    }
                }.setNegativeButton("Cancel") { dialog, which -> }.show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("00 2456456676",null, smsInfo.text, pendingIntent, null)

    }

}
