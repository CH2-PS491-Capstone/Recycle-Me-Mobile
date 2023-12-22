package com.bangkit.recycleme.ui.scan

import android.animation.AnimatorInflater
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.ArticleAdapter
import com.bangkit.recycleme.databinding.ActivityScanBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ml.WasteclassModel
import com.bangkit.recycleme.ui.article.ArticleViewModel
import com.bangkit.recycleme.ui.camera.CameraActivity
import com.bangkit.recycleme.ui.detail.DetailActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class ScanActivity : AppCompatActivity() {
    private val storyViewModel by viewModels<ArticleViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityScanBinding
    private lateinit var bitmap: Bitmap
    private var currentImageUri: Uri? = null

    private lateinit var storyAdapter: ArticleAdapter

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERAX_RESULT) {
            val uri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            uri?.let {
                try {
                    // Load the original bitmap without rotation
                    val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

                    // Get the orientation of the image
                    val inputStream = contentResolver.openInputStream(uri)
                    val exif = inputStream?.let { it1 -> ExifInterface(it1) }
                    val orientation =
                        exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                    // Rotate the bitmap based on the orientation
                    val rotatedBitmap = orientation?.let { it1 ->
                        rotateBitmap(originalBitmap,
                            it1
                        )
                    }

                    // Set the rotated bitmap to the ImageView
                    binding.previewImageView.setImageBitmap(rotatedBitmap)

                    // Update the class-level bitmap property with the rotated bitmap
                    if (rotatedBitmap != null) {
                        bitmap = rotatedBitmap
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Failed to load image")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.scan_menu_appbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.root.translationY = 1000f

        val slideUpAnimation = AnimatorInflater.loadAnimator(this, R.animator.slide_up)
        slideUpAnimation.setTarget(binding.root)

        slideUpAnimation.start()

        val labels = application.assets.open("labels.txt").bufferedReader().readLines()

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        binding.cameraButton.setOnClickListener { showPopup() }
//        binding.cameraButton.setOnClickListener { startCamera() }
//        binding.cameraButton.setOnClickListener { startCameraX() }

        binding.scanButton.setOnClickListener{
            if (!::bitmap.isInitialized) {
                showToast("Pilih gambar terlebih dahulu")
                return@setOnClickListener
            } else {
                ::bitmap.isInitialized
            }

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            tensorImage = imageProcessor.process(tensorImage)

            val model = WasteclassModel.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            outputFeature0.forEachIndexed{index, fl ->
                if(outputFeature0[maxIdx] < fl) {
                    maxIdx = index
                }
            }

            binding.namaSampah.setText(labels[maxIdx])
            val overviewText = getOverviewText(maxIdx)
            binding.kategoriSampah.text = overviewText

            loadCategoryImage(maxIdx)

            val recyclerView = binding.rvPhone
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            storyAdapter = ArticleAdapter { view ->
                val position = recyclerView.getChildAdapterPosition(view)
                val clickedUser = storyAdapter.getStory(position)
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("id", clickedUser.id)
                startActivity(intent)
            }

            recyclerView.adapter = storyAdapter

            storyViewModel.storyList.observe(this, Observer { stories ->
                storyAdapter.setStories(stories)
            })

            getFilterText(maxIdx, recyclerView)


            // Releases model resources if no longer used.
            model.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                try {
                    // Load the original bitmap without rotation
                    val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

                    // Get the orientation of the image
                    val inputStream = contentResolver.openInputStream(uri)
                    val exif = inputStream?.let { it1 -> ExifInterface(it1) }
                    val orientation =
                        exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                    // Rotate the bitmap based on the orientation
                    val rotatedBitmap = orientation?.let { it1 ->
                        rotateBitmap(originalBitmap,
                            it1
                        )
                    }

                    // Set the rotated bitmap to the ImageView
                    binding.previewImageView.setImageBitmap(rotatedBitmap)

                    // Update the class-level bitmap property with the rotated bitmap
                    if (rotatedBitmap != null) {
                        bitmap = rotatedBitmap
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Failed to load image")
                }
            }
        }
    }


    private fun getOverviewText(categoryIndex: Int): String {
        return when (categoryIndex) {
            0 -> "Pilah sampahmu dan buang di tempat sampah berwarna merah seperti gambar di atas ya.\n\nTahukah kamu? Elekronik merupakan jenis sampah B3. Tindakan sederhana seperti mendaur ulang elektronik yang tidak terpakai dapat membantu mengurangi dampak buruk limbah elektronik terhadap lingkungan.\n\nElektronik, seperti komputer, smartphone, dan perangkat elektronik lainnya, mengandung berbagai bahan kimia berbahaya yang dapat mencemari tanah dan air jika dibuang secara tidak benar.\n\nKamu juga bisa membantu daur ulang dengan mengikuti beberapa langkah pada artikel di bawah ini. Setiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            1 -> "Pilah sampahmu dan buang di tempat sampah berwarna kuning seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk limbah kaca terhadap lingkungan. Kaca adalah jenis material anorganik yang dapat memakan waktu ratusan tahun untuk terurai alami di alam bebas.\n\nBila kaca dibuang secara sembarangan, dapat menciptakan masalah lingkungan karena tidak hanya sulit terurai, tetapi juga dapat menciptakan risiko cedera dan mencemari lingkungan sekitarnya. Dengan memilah sampah, khususnya kaca, kita membantu memastikan bahwa material ini dapat didaur ulang dengan benar.\n\nProses daur ulang kaca melibatkan peleburan dan pemrosesan ulang, mengurangi kebutuhan akan produksi kaca baru yang memerlukan bahan baku alam seperti pasir. Dengan cara ini, kita dapat mengurangi tekanan terhadap sumber daya alam dan menurunkan emisi gas rumah kaca yang terkait dengan produksi kaca baru.\n\nSetiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            2 -> "Pilah sampahmu dan buang di tempat sampah berwarna kuning seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk limbah tekstil atau pakaian terhadap lingkungan. Pakaian yang tidak lagi digunakan atau rusak seringkali berakhir di tempat pembuangan sampah, menyebabkan penumpukan sampah yang tidak perlu. Material tekstil seperti kain juga dapat memakan waktu lama untuk terurai dan memberikan kontribusi terhadap masalah lingkungan.\n\nDengan memilah sampah, terutama pakaian atau kain yang tidak terpakai, kita dapat membantu mengurangi dampak negatifnya. Salah satu cara yang efektif adalah dengan mendaur ulang pakaian.\n\nPakaian yang masih dapat digunakan dapat disumbangkan atau dijual kembali, sementara kain yang tidak dapat digunakan lagi dapat didaur ulang menjadi produk baru atau bahan lain seperti isolasi atau kain daur ulang."
            3 -> "Pilah sampahmu dan buang di tempat sampah berwarna kuning seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk limbah kardus terhadap lingkungan. Kardus, meskipun merupakan bahan yang dapat terurai alami, tetapi merupakan sampah jenis anorganik, seringkali berakhir di tempat pembuangan sampah jika tidak dikelola dengan benar.\n\nPemrosesan kardus di tempat pembuangan akhir dapat memerlukan waktu yang cukup lama, dan jika terbakar, dapat menyebabkan emisi gas beracun ke udara.\n\nDengan memilah sampah, terutama kardus, kita dapat memastikan bahwa bahan ini dapat didaur ulang dengan benar. Daur ulang kardus melibatkan proses pemecahan dan pemrosesan ulang, yang membantu mengurangi kebutuhan akan pohon sebagai bahan baku untuk pembuatan kardus baru. Dengan cara ini, kita dapat menjaga hutan dan ekosistem yang penting untuk keseimbangan lingkungan.\n\nKamu juga bisa membantu daur ulang dengan mengikuti beberapa langkah pada artikel di bawah ini. Setiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            4 -> "Pilah sampahmu dan buang di tempat sampah berwarna kuning seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk limbah kertas terhadap lingkungan. Kertas, meskipun terbuat dari bahan organik seperti pulp kayu, tetapi pada dasarnya merupakan jenis sampah anorganik, dapat memberikan kontribusi terhadap masalah lingkungan jika tidak dikelola dengan baik.\n\nProses pembuatan kertas baru seringkali memerlukan penebangan pohon secara besar-besaran, yang dapat merusak hutan dan ekosistem alam. Dengan memilah sampah, terutama kertas, kita dapat memastikan bahwa kertas yang sudah tidak terpakai dapat didaur ulang dengan benar.\n\nDaur ulang kertas melibatkan proses pemrosesan kertas bekas untuk diubah menjadi produk kertas baru. Dengan cara ini, kita dapat mengurangi tekanan terhadap sumber daya alam dan hutan, serta mengurangi jumlah limbah yang berakhir di tempat pembuangan sampah.\n\nKamu juga bisa membantu daur ulang dengan mengikuti beberapa langkah pada artikel di bawah ini. Setiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            5 -> "Pilah sampahmu dan buang di tempat sampah berwarna kuning seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk limbah logam terhadap lingkungan. Logam, seperti besi, aluminium, dan baja, adalah bahan yang dapat didaur ulang dengan efisien dan memiliki potensi untuk mengurangi tekanan terhadap sumber daya alam.\n\nKetika logam dibuang ke tempat pembuangan sampah, tidak hanya membuang sumber daya yang berharga, tetapi juga dapat menciptakan masalah pencemaran lingkungan.\n\nDengan memilah sampah, terutama logam, kita dapat memastikan bahwa logam dapat diarahkan ke proses daur ulang yang tepat. Proses daur ulang logam melibatkan peleburan dan pemrosesan ulang, yang dapat mengurangi kebutuhan akan pertambangan logam baru.\n\nDengan cara ini, kita tidak hanya membantu melestarikan sumber daya alam tetapi juga mengurangi dampak ekstraksi logam terhadap lingkungan.\n\nKamu juga bisa membantu daur ulang dengan mengikuti beberapa langkah pada artikel di bawah ini. Setiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            6 -> "Pilah sampahmu dan buang di tempat sampah berwarna hijau seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk limbah organik terhadap lingkungan. Limbah organik, seperti sisa makanan dan bahan organik lainnya, dapat menghasilkan gas metana jika terbuang ke tempat pembuangan sampah, yang merupakan salah satu gas rumah kaca yang berkontribusi pada perubahan iklim.\n\nDengan memilah sampah, khususnya limbah organik, kita dapat mengarahkannya ke proses daur ulang yang tepat. Proses daur ulang limbah organik melibatkan kompos atau pengomposan, di mana limbah organik diurai secara alami menjadi pupuk yang berguna untuk tanah.\n\nDengan melakukan ini, kita tidak hanya mengurangi jumlah limbah yang masuk ke tempat pembuangan sampah, tetapi juga menghasilkan pupuk alami yang dapat membantu meningkatkan kesuburan tanah.\n\nKamu juga bisa membantu daur ulang dengan mengikuti beberapa langkah pada artikel di bawah ini. Setiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            7 -> "Pilah sampahmu dan buang di tempat sampah berwarna kuning seperti gambar di atas ya.\n\nTahukah kamu? Tindakan sederhana ini dapat membantu mengurangi dampak buruk plastik terhadap lingkungan. Plastik adalah jenis sampah anorganik dengan bahan yang sulit terurai dan dapat mencemari tanah, air, dan udara jika tidak dikelola dengan baik.\n\nKetika kita memilah sampah, kita membantu memastikan bahwa plastik dapat didaur ulang.\n\nSelain itu, perlu diingat bahwa produksi plastik juga berkontribusi terhadap masalah perubahan iklim. Proses pembuatan plastik menggunakan sumber daya energi fosil dan melepaskan gas rumah kaca ke atmosfer. Dengan mengurangi penggunaan plastik dan mendukung alternatif ramah lingkungan, kita dapat berperan dalam upaya global untuk menanggulangi perubahan iklim.\n\nKamu juga bisa membantu daur ulang dengan mengikuti beberapa langkah pada artikel di bawah ini. Setiap sampah yang berhasil kamu daur ulang, kamu akan mendapatkan coin yang tentunya bisa kamu tukar dengan saldo E-Wallet."
            else -> "Gambar tidak dikenali. Pastikan kamu menggunakan gambar barang atau makanan saja."
        }
    }

    private fun loadCategoryImage(categoryIndex: Int) {
        val imageView = binding.iconTrash
        when (categoryIndex) {
            0 -> imageView.setImageResource(R.drawable.baseline_restore_from_trash_24_b3)
            1,2,3,4,5,7 -> imageView.setImageResource(R.drawable.baseline_restore_from_trash_24_anorganic)
            6 -> imageView.setImageResource(R.drawable.baseline_restore_from_trash_24_organic)
            else -> imageView.setImageResource(R.drawable.baseline_recycling_24)
        }
    }

    private fun getFilterText(categoryIndex: Int, recyclerView: RecyclerView) {
        val category = when (categoryIndex) {
            0 -> "elektronik"
            1 -> "kaca"
            2 -> "kain"
            3 -> "kardus"
            4 -> "kertas"
            5 -> "logam"
            6 -> "organik"
            7 -> "plastik"
            else -> "tidak diketahui"
        }

        filterListByCategory(category)
    }

    private fun filterListByCategory(category: String) {
        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }
        storyViewModel.searchFilter(token, "", category)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, 100)
    }

    private fun showPopup() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Pilih Gambar")
        builder.setMessage("Melalui galeri atau kamera")

        // Set up the buttons
        builder.setPositiveButton("Galeri") { _, _ ->
            startGallery()
        }

        builder.setNegativeButton("Kamera") { _, _ ->
            startCameraX()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}