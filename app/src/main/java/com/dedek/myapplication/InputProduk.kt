package com.dedek.myapplication

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.bumptech.glide.Glide
import com.dedek.myapplication.databinding.ActivityInputProdukBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.json.JSONObject
import java.io.File


class InputProduk : AppCompatActivity() {
    lateinit var binding: ActivityInputProdukBinding
    var file: File? = null
    var filename = ""
    var progressDialog : ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputProdukBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Sedang Proses")
        progressDialog!!.setMessage(" Mohon Tunggu ")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setCancelable(false)
        progressDialog!!.isIndeterminate = true
        AndroidNetworking.initialize(this);

        binding.BtnPilih.setOnClickListener {
            var folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            if (!folder.exists()) folder.mkdir()
            file = File(folder.absolutePath, "imageProduk")

            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                      if (p0?.areAllPermissionsGranted()!!){
                          ImagePicker.with(this@InputProduk)
                              .cameraOnly()
                              .cropSquare()
                              .compress(512)
                              .saveDir(file!!)
//                              .saveDir(File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!, "ImagePicker"))
                              .maxResultSize(1080,1080)
//                              .start()

                              .start { resultCode, data ->
                                  if (resultCode == Activity.RESULT_OK){
                                      Log.d("InputProduk", "result data : ${data?.data}")
                                      Glide.with(this@InputProduk)
                                          .load(data?.data)
                                          .into(binding.IvImgproduk)
                                      filename = File (data?.data?.path).name

                                      Log.d("InputProduk", "result filename : $filename")

                                  } else if (resultCode == ImagePicker.RESULT_ERROR){
                                      Toast.makeText(this@InputProduk, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                                  }

                              }

                      }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }


                }).check()
            return@setOnClickListener
        }
        binding.BtnKirim.setOnClickListener {
            when {
                binding.EtInputNamaProduk.text.toString().isEmpty() -> binding.EtInputNamaProduk.error = "Tidak Boleh Kosong !! "
                binding.EtInputHarga.text.toString().isEmpty() -> binding.EtInputHarga.error = " tidak Boleh Kosong !! "
                filename.isNullOrEmpty() -> Toast.makeText(this, "Silahkan pilih foto dulu !! ", Toast.LENGTH_SHORT).show()
                else -> uploadProduk()
            }
            return@setOnClickListener
        }
    }

    private fun uploadProduk() {
        val FilePath = "${file}/${filename}"
        val namefile = File(FilePath)

        var filesUpload = File("/storage/emulated/0/Android/data/com.dedek.myapplication/files/Pictures/imageProduk/IMG_20221022_182042958.jpg")


        Log.d("InputProduk","Respon nameFIle dan filePath $FilePath/$namefile")

        progressDialog?.show()
        AndroidNetworking.upload("http://belipulsabeta.purja.web.id/public/api/inputproduk")
            .addHeaders("Authorization","Bearer01USgSmbrZo6UdFd")
            .addMultipartParameter("nama_produk",binding.EtInputNamaProduk.text.toString())
            .addMultipartParameter("harga",binding.EtInputHarga.text.toString())
            .addMultipartParameter("status","active")
            .addMultipartFile("image",namefile)
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener(object : UploadProgressListener{
                override fun onProgress(p0: Long, p1: Long) {
                 Log.d("InputProduk","po $p0  dan p1 $p1")
                }

            })
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(p0: JSONObject?) {
                    progressDialog?.dismiss()
                    var respon = p0.toString()
                    Log.d("InputProduk","Respon Input $respon")
                    var json = JSONObject(respon)
                    val ApiStatus = json.getInt("api_status")
                    val ApiMessage = json.getString("api_message")
                    if (ApiStatus.equals(1)){
                        val intent = Intent(this@InputProduk,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else{
                        Toast.makeText(this@InputProduk, ApiMessage, Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onError(p0: ANError?) {
                    progressDialog?.dismiss()
                    Log.d("InputProduk", p0.toString())
                    Toast.makeText(this@InputProduk, " Silahkan Cek Kondisi Internet Anda !! ", Toast.LENGTH_SHORT).show()

                }

            })
    }


}
