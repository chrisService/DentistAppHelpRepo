package com.dentify.dentify.ui.fragments.videoCall

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.databinding.FragmentVideoCallBinding
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.CustomTouchListener
import com.dentify.dentify.util.NofificationUtil
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.*
import com.twilio.video.Video
import com.twilio.video.ktx.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VideoCallFragment : Fragment() {
    lateinit var binding: FragmentVideoCallBinding
    val viewModel: VideoCallViewModel by viewModels()
    private var densityConverter: Float = 0.0F
    private var fullScreenOn: Boolean = false
    var trackOnPlace: Boolean = true
    private var cameraON: Boolean = true
    var clinitianCameraSubscribed: Boolean = false
    private var muteOn: Boolean = false
    lateinit var videoViewDoctor: VideoView
    lateinit var videoViewPatient: VideoView
    private var networkVideoTrack: VideoTrack? = null
    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private lateinit var connectBtn: FloatingActionButton
    private lateinit var disconnectBtn: ImageButton
    private lateinit var muteBtn: ImageButton
    private lateinit var switchBtn: ImageButton
    private lateinit var cameraSwitchBtn: ImageButton
    private lateinit var clickText: TextView
    private lateinit var tvClinician: TextView
    private lateinit var tvClinic: TextView
    private lateinit var ivMuteIndicator: ImageView
    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null
    private val cameraCapturerCompat by lazy {
        CameraCapturerCompat(requireContext(), CameraCapturerCompat.Source.FRONT_CAMERA)
    }
    private var isCallDisconnected: Boolean = false

    //twilio
    //should get this from API
    var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoCallBinding.inflate(layoutInflater)
        populateTextStatusField()
        initializeView()
        showAudioDevices()
        setSpeakerOn()
        setButtonClicks()
        moveSmallVideoView()
        requestCameraPermission.launch(Manifest.permission.CAMERA)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding = FragmentVideoCallBinding.inflate(layoutInflater)
    }

    private fun populateTextStatusField(){
        if(arguments?.getString(Constants.ROOM_ID) != null){
            val roomID = arguments?.getString(Constants.ROOM_ID)
            StorageWrapper.appointmentId = roomID
            getTwilioToken(roomID!!)
        }
    }

    var factor = 1.0f

    private fun setButtonClicks(){
        binding.btnFullScreen.setOnClickListener {
            if (fullScreenOn == true){
                setFullScreenOff()
            }else{
                setFullScreenOn()
            }
        }
        videoViewDoctor.setOnTouchListener(object: View.OnTouchListener{
            val gesture = GestureDetector(requireContext(), object: GestureDetector.SimpleOnGestureListener(){
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    changeVideoTracks()
                    return super.onDoubleTap(e)
                }
            })
            val scaleDetector = ScaleGestureDetector(requireContext(), object: ScaleGestureDetector.OnScaleGestureListener{
                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    val scaleFactor = detector!!.scaleFactor -1
                    factor += scaleFactor
                    if (factor > 1 && factor < 5){
                        videoViewDoctor.scaleX= factor
                        videoViewDoctor.scaleY= factor
                    }
                    return true
                }
                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                    return true
                }
                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                }
            })
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                gesture.onTouchEvent(event)
                scaleDetector.onTouchEvent(event)
                return true
            }
        })
        muteBtn.setOnClickListener {
            if (muteOn == false){
                if (localAudioTrack != null){
                    localAudioTrack!!.enabled = false
                    Toast.makeText(requireContext(), getString(R.string.mic_muted), Toast.LENGTH_SHORT).show()
                    muteBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mute_off))
                    muteBtn.background.setTint(ContextCompat.getColor(requireContext(), R.color.videoCallEndIconBackground))
                    muteOn = true
                }
            }else{
                if (localAudioTrack != null){
                    localAudioTrack!!.enabled = true
                    Toast.makeText(requireContext(), getString(R.string.mic_on), Toast.LENGTH_SHORT).show()
                    muteBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mute_on))
                    muteBtn.background.setTint(ContextCompat.getColor(requireContext(), R.color.videoCallIconBackground))
                    muteOn = false
                }
            }
        }
        switchBtn.setOnClickListener {
            cameraCapturerCompat.switchCamera()
        }
        cameraSwitchBtn.setOnClickListener {
            cameraOnnOff()
        }
    }

    private fun cameraOnnOff(){
        if (cameraON == true){
            if (trackOnPlace == true){
                hidePatientVideoTrack(videoViewPatient)
            }else{
                trackOnPlace = true
                hidePatientVideoTrack(videoViewDoctor)
                showClinicianVideoTrack(videoViewDoctor)
            }
            cameraON = false
            cameraSwitchBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_camera_off))
            cameraSwitchBtn.background.setTint(ContextCompat.getColor(requireContext(), R.color.videoCallEndIconBackground))
        }else{
            showPatientVideoTrack(videoViewPatient)
            cameraON = true
            cameraSwitchBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_camera_on))
            cameraSwitchBtn.background.setTint(ContextCompat.getColor(requireContext(), R.color.videoCallIconBackground))
        }
    }

    fun changeVideoTracks(){
        if (trackOnPlace == false && clinitianCameraSubscribed == true && cameraON == true){
            if (networkVideoTrack != null){
                showClinicianVideoTrack(videoViewDoctor)
                showPatientVideoTrack(videoViewPatient)
                trackOnPlace = true
            }
        }else if(trackOnPlace == true && clinitianCameraSubscribed == true && cameraON == true){
            if (networkVideoTrack != null){
                showClinicianVideoTrack(videoViewPatient)
                showPatientVideoTrack(videoViewDoctor)
                trackOnPlace = false
            }
        }
    }

    private fun setFullScreenOn(){
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        params.topToBottom = ConstraintSet.PARENT_ID
        params.bottomToBottom = ConstraintSet.PARENT_ID
        params.endToStart = ConstraintSet.PARENT_ID
        params.startToEnd = ConstraintSet.PARENT_ID
        params.setMargins(0,0, 0, 0)
        factor = 1.0f
        videoViewDoctor.scaleX= factor
        videoViewDoctor.scaleY= factor
        videoViewDoctor.layoutParams = params
        binding.btnFullScreen.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_full_screen_off))
        fullScreenOn = true
    }
    private fun setFullScreenOff(){
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        params.topToBottom = ConstraintSet.PARENT_ID
        params.bottomToBottom = ConstraintSet.PARENT_ID
        params.endToStart = ConstraintSet.PARENT_ID
        params.startToEnd = ConstraintSet.PARENT_ID
        params.setMargins(0,200*densityConverter.toInt(), 0, 200*densityConverter.toInt())
        factor = 1.0f
        videoViewDoctor.scaleX= factor
        videoViewDoctor.scaleY= factor
        videoViewDoctor.layoutParams = params
        binding.btnFullScreen.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_full_screen_on))
        fullScreenOn = false
    }

    private fun moveSmallVideoView(){
        val parentView = binding.fragmentParent
        parentView.viewTreeObserver.addOnGlobalLayoutListener { binding.rlSmallVideoLayout.setOnTouchListener(CustomTouchListener(parentView.width, parentView.height)) }
    }

    private fun setSpeakerOn(){
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.apply {
            isSpeakerphoneOn = true
            audioManager.setParameters(Constants.NOISE_SUPRESSION_ON)
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        }
    }

    private fun getTwilioToken(roomName: String){
        viewModel.getTwilioToken(object: ViewModelApiListener {
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                accessToken = viewModel.twilioTokenResponse
                connectToRoom(roomName)
            }
            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initializeView(){
        videoViewDoctor = binding.videoViewDoctor
        videoViewPatient = binding.videoViewPatient
        connectBtn = binding.btnConnect
        disconnectBtn = binding.btnDisconnect
        muteBtn = binding.btnMute
        switchBtn = binding.btnCamSwitch
        cameraSwitchBtn = binding.btnCameraSwitch
        clickText = binding.tvClickText
        tvClinic = binding.tvClinic
        tvClinician = binding.tvClinician
        ivMuteIndicator = binding.ivMuteIndicator
        tvClinic.text = StorageWrapper.getPatientsProfileResponse(requireContext())?.clinic?.clinicName
        tvClinician.text = StorageWrapper.getPatientsProfileResponse(requireContext())?.clinician?.fullName
        densityConverter = requireContext().resources.displayMetrics.density
        binding.tvInitials.text = Util.initials(StorageWrapper.getPatientsProfileResponse(requireContext())?.clinician?.fullName)
        if (StorageWrapper.getPatientsProfileResponse(requireContext())?.clinician?.profileImageUri != null){
            Glide.with(requireContext()).load(StorageWrapper.getPatientsProfileResponse(requireContext())?.clinician?.profileImageUri).circleCrop().into(binding.ivClinitianImage)
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            requestBluetoothPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
    }

    private val requestBluetoothPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    private val requestAudioPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        createAudioAndVideoTracks()
    }

    private val audioSwitch by lazy {
        AudioSwitch(
            requireContext(), preferredDeviceList = listOf(
                AudioDevice.BluetoothHeadset::class.java,
                AudioDevice.WiredHeadset::class.java, AudioDevice.Speakerphone::class.java, AudioDevice.Earpiece::class.java
            )
        )
    }

    private fun showAudioDevices() {

        binding.btnAudio.setOnClickListener {
            val availableAudioDevices = audioSwitch.availableAudioDevices

            audioSwitch.selectedAudioDevice?.let { selectedDevice ->
                val selectedDeviceIndex = availableAudioDevices.indexOf(selectedDevice)
                val audioDeviceNames = ArrayList<String>()

                for (a in availableAudioDevices) {
                    audioDeviceNames.add(a.name)
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("select device")
                    .setSingleChoiceItems(
                        audioDeviceNames.toTypedArray<CharSequence>(),
                        selectedDeviceIndex
                    ) { dialog, index ->
                        dialog.dismiss()
                        val selectedAudioDevice = availableAudioDevices[index]
                        audioSwitch.selectDevice(selectedAudioDevice)
                    }.create().show()
            }
        }

    }

    fun connectToRoom(roomName: String){
        connectBtn.setOnClickListener {

            audioSwitch.start { _, selectedAudioDevice ->
                audioSwitch.selectDevice(selectedAudioDevice)
            }
            audioSwitch.activate()

            connect(roomName)

            disconnectBtn.visibility = View.VISIBLE
            connectBtn.visibility = View.INVISIBLE
            clickText.visibility = View.GONE
        }

        disconnectBtn.setOnClickListener {
            room?.disconnect()
            isCallDisconnected = true
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
            findNavController().navigate(R.id.videoCallReviewFragment, null, navOptions)
        }
    }

    private fun connect(roomName: String){
        room = connect(requireContext(), accessToken, roomListener) {
            roomName(roomName)

            audioTracks(listOf(localAudioTrack))
            videoTracks(listOf(localVideoTrack))

            preferAudioCodecs(listOf(audioCodec))
            preferVideoCodecs(listOf(videoCodec))

            encodingParameters(encodingParameters)
            enableAutomaticSubscription(true)
        }
    }

    fun connect(
        context: Context,
        token: String,
        roomListener: Room.Listener,
        connectOptionsBuilder: ConnectOptionsBuilder? = null
    ) = Video.connect(context, createConnectOptions(token, connectOptionsBuilder), roomListener)

    private val roomListener = object : Room.Listener {
        override fun onConnected(room: Room) {
            localParticipant = room.localParticipant
            // Only one participant is supported
            room.remoteParticipants.firstOrNull()?.let { addRemoteParticipant(it) }
        }

        override fun onReconnected(room: Room) {
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
        }

        override fun onConnectFailure(room: Room, e: TwilioException) {
        }

        override fun onDisconnected(room: Room, e: TwilioException?) {
            localParticipant = null
        }

        override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
            addRemoteParticipant(participant)
            clickText.visibility = View.GONE
        }

        override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
            clickText.text = getString(R.string.clinician_disconnected)
            clickText.visibility = View.VISIBLE
        }

        override fun onRecordingStarted(room: Room) {
        }

        override fun onRecordingStopped(room: Room) {
        }
    }

    override fun onDestroy() {
        room?.disconnect()
        audioSwitch.stop()
        localVideoTrack?.enabled = false
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (isCallDisconnected == false){
            NofificationUtil.sendNotification(requireContext())
        }
    }


    private fun createAudioAndVideoTracks() {

        // Share your microphone
        localAudioTrack = createLocalAudioTrack(requireContext(), true)

        // Share your camera
        localVideoTrack = createLocalVideoTrack(
            requireContext(),
            true,
            cameraCapturerCompat
        )

        if (trackOnPlace == true){
            showPatientVideoTrack(videoViewPatient)
        }else{
            showPatientVideoTrack(videoViewDoctor)
        }
    }

    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {

        /*
         * Add participant renderer
         */
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { addRemoteParticipantVideo(it) }
            }
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(participantListener)
    }

    private fun addRemoteParticipantVideo(videoTrack: VideoTrack) {
        videoViewDoctor.mirror = true
        networkVideoTrack = videoTrack
        if(trackOnPlace == true){
            showClinicianVideoTrack(videoViewDoctor)
        }else{
            showClinicianVideoTrack(videoViewPatient)
        }
    }

    private val audioCodec: AudioCodec
        get() {
            val audioCodecName = Constants.PREF_AUDIO_CODEC_DEFAULT

            return when (audioCodecName) {
                IsacCodec.NAME -> IsacCodec()
                OpusCodec.NAME -> OpusCodec()
                PcmaCodec.NAME -> PcmaCodec()
                PcmuCodec.NAME -> PcmuCodec()
                G722Codec.NAME -> G722Codec()
                else -> OpusCodec()
            }
        }
    private val videoCodec: VideoCodec
        get() {
            val videoCodecName = Constants.PREF_VIDEO_CODEC_DEFAULT

            return when (videoCodecName) {
                Vp8Codec.NAME -> {
                    val simulcast = Constants.PREF_VP8_SIMULCAST_DEFAULT
                    Vp8Codec(simulcast)
                }
                H264Codec.NAME -> H264Codec()
                Vp9Codec.NAME -> Vp9Codec()
                else -> Vp8Codec()
            }
        }

    private val encodingParameters: EncodingParameters
        get() {
            val maxAudioBitrate = 0
            val maxVideoBitrate = 0

            return EncodingParameters(maxAudioBitrate, maxVideoBitrate)
        }

    /*
     * RemoteParticipant events listener
     */
    private val participantListener = object : RemoteParticipant.Listener {

        override fun onAudioTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {}

        override fun onAudioTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {}

        override fun onAudioTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {}

        override fun onAudioTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            twilioException: TwilioException
        ) {}

        override fun onAudioTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {}

        override fun onVideoTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {}

        override fun onVideoTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {}

        override fun onVideoTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            addRemoteParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            twilioException: TwilioException
        ) {}

        override fun onVideoTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            clinitianCameraSubscribed = false
            if (trackOnPlace == true){
                hideClinicianVideoTrack(videoViewDoctor)
            }else{
                hideClinicianVideoTrack(videoViewPatient)
            }
        }

        override fun onDataTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {}

        override fun onDataTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {}

        override fun onDataTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) { }

        override fun onDataTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            twilioException: TwilioException
        ) {}

        override fun onDataTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) { }

        override fun onAudioTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            ivMuteIndicator.visibility = View.GONE
        }

        override fun onAudioTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            ivMuteIndicator.visibility = View.VISIBLE
        }

        override fun onVideoTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) { }

        override fun onVideoTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {}
    }


    private fun showClinicianVideoTrack(videoView: VideoView){
        localVideoTrack?.removeSink(videoView)
        videoView.clearImage()
        networkVideoTrack?.addSink(videoView)
        binding.relativeLayout.visibility = View.GONE
        clinitianCameraSubscribed = true
    }
    private fun hideClinicianVideoTrack(videoView: VideoView){
        networkVideoTrack?.removeSink(videoView)
        videoView.clearImage()
        binding.relativeLayout.visibility = View.VISIBLE
        clinitianCameraSubscribed = false
    }
    private fun showPatientVideoTrack(videoView: VideoView){
        networkVideoTrack?.removeSink(videoView)
        videoView.clearImage()
        localVideoTrack?.addSink(videoView)
        localVideoTrack?.enabled = true
        if(trackOnPlace == true){
            videoViewPatient.visibility = View.VISIBLE
            binding.rlSmallVideoLayout.visibility = View.VISIBLE
        }
    }
    private fun hidePatientVideoTrack(videoView: VideoView){
        localVideoTrack?.removeSink(videoView)
        videoView.clearImage()
        localVideoTrack?.enabled = false
        if(trackOnPlace == true){
            videoViewPatient.visibility = View.GONE
            binding.rlSmallVideoLayout.visibility = View.GONE
        }
    }
}