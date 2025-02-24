package br.edu.puccampinas.PI3_ES_2024_Time17

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity() {
    private var qrCodeDetected = false
    private lateinit var futuroProvedorCamera: ListenableFuture<ProcessCameraProvider>
    private lateinit var seletorCamera: CameraSelector
    private lateinit var executorCapturaImg: ExecutorService
    private lateinit var scannerCodigoBarras: BarcodeScanner
    private lateinit var visualizacaoCamera: PreviewView

    private val tempoReq = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), tempoReq)
        }

        futuroProvedorCamera = ProcessCameraProvider.getInstance(this)
        seletorCamera = CameraSelector.DEFAULT_BACK_CAMERA
        executorCapturaImg = Executors.newSingleThreadExecutor()
        scannerCodigoBarras = BarcodeScanning.getClient()

        iniciarCamera()
    }

    private fun iniciarCamera() {
        futuroProvedorCamera.addListener({
            val provedorCamera = futuroProvedorCamera.get()
            visualizacaoCamera = findViewById(R.id.cameraPreview)

            val visualizacao = Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(visualizacaoCamera.surfaceProvider)
            }
            visualizacaoCamera.post {
                val rect = Rect()
                visualizacaoCamera.getGlobalVisibleRect(rect)
                val layoutParams = FrameLayout.LayoutParams(
                    (rect.width() * 0.8).toInt(),
                    (rect.height() * 0.5).toInt()
                )
                layoutParams.leftMargin = (rect.width() * 0.1).toInt()
                layoutParams.topMargin = (rect.height() * 0.25).toInt()

                val scanRect = FrameLayout(this)
                scanRect.layoutParams = layoutParams
                scanRect.background = ContextCompat.getDrawable(this, R.drawable.rectangle_border)
                findViewById<FrameLayout>(R.id.scanRect).addView(scanRect)
            }

            val analiseImagem = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(executorCapturaImg) { imageProxy -> analisarImagem(imageProxy) }
                }

            try {
                provedorCamera.unbindAll()
                provedorCamera.bindToLifecycle(this, seletorCamera, visualizacao, analiseImagem)
            } catch (e: Exception) {
                Log.e("CameraPreview", "A Câmera não está funcionando", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analisarImagem(imageProxy: ImageProxy) {
        if (qrCodeDetected) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: return
        val imagem = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)


        scannerCodigoBarras.process(imagem)
            .addOnSuccessListener { codigosBarras ->
                for (codigoBarras in codigosBarras) {
                    val valorQRCode = codigoBarras.rawValue ?: continue
                    qrCodeDetected = true
                    showSelectionDialog(valorQRCode)
                    break
                }
                imageProxy.close()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao processar o QRcode", Toast.LENGTH_SHORT).show()
                imageProxy.close()
            }
    }

    private fun showSelectionDialog(valorQRCode: String) {
        val options = arrayOf("1 pessoa", "2 pessoas")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha um valor")
            .setItems(options) { _, which ->
                val selectedValue = options[which]
                passarValor(selectedValue, valorQRCode)
            }
            .setCancelable(false)
            .show()
    }

    private fun passarValor(selectedValue: String, qrCodeValue: String) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("SelectedValue", selectedValue)
        intent.putExtra("QRCodeValue", qrCodeValue)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == tempoReq && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarCamera()
        }
    }
}
