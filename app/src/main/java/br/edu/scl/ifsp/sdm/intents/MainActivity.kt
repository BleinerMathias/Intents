package br.edu.scl.ifsp.sdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        private const val PARAMETER_REQUEST_CODE = 0
    }


    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var  parameterActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var callPhoneActivityResultLauncher: ActivityResultLauncher<String>
    private lateinit var pickImageActivityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        // Inicialização do parameter
        parameterActivityResultLauncher = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()){ result ->
            // Será executado somente quando a tela chamada fechar
            if(result.resultCode == RESULT_OK){
                result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                    activityMainBinding.parameterTv.text = it
                }
            }
        }


        callPhoneActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){permissionGranted ->
            if(permissionGranted){
                callPhone(true)
            }else{
                Toast.makeText(this,
                    getString(R.string.permission_required_to_call), Toast.LENGTH_SHORT).show()
            }

        }

        pickImageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            with(result) {
                if (result.resultCode == RESULT_OK) {
                    data?.data?.also {
                        activityMainBinding.parameterTv.text = it.toString()
                        startActivity(Intent(ACTION_VIEW).apply { data = it })
                    }
                }
            }
        }


        activityMainBinding.apply{
            parameterBt.setOnClickListener{
                // Explícita (qual classe vai tratar)
                val parameterIntent = Intent(this@MainActivity,ParameterActivity::class.java).apply {
                    putExtra(PARAMETER_EXTRA,parameterTv.text)
                }
                //startActivityForResult(parameterIntent,PARAMETER_REQUEST_CODE)
                parameterActivityResultLauncher.launch(parameterIntent)
            }
        }
    }

    // forma legada
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PARAMETER_REQUEST_CODE && resultCode == RESULT_OK){
            data?.getStringExtra(PARAMETER_EXTRA)?.also {
                activityMainBinding.parameterTv.text = it
            }
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openActivityMi -> {
                // Passamos uma action ao contrário do exemplo anterior
                val parameterIntent = Intent("OPEN_PARAMETER_ACTIVITY_ACTION").apply {
                    putExtra(PARAMETER_EXTRA, activityMainBinding.parameterTv.text)
                }
                parameterActivityResultLauncher.launch(parameterIntent)
                true
            }
            R.id.viewMi -> {

                // Criar uma intent action_view carregando uma url (identificador de recurso)
                // URL subpadrão de URI -> Indentificador de recursos unicos
                startActivity(browserIntent());

                true
            }

            R.id.callMi -> {

                // até a API 23, era solicitado durante a instalação para utilizar de recursos específicos.
                // Chamada é perigosa.

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED){
                        // CHAMAR
                        callPhone(true)
                    }else{
                        // Solicitar a permissão
                        callPhoneActivityResultLauncher.launch(CALL_PHONE)
                    }
                }else{
                    // Chamar
                    callPhone(true)
                }

                true
            }

            R.id.dialMi -> {
                callPhone(false)
                true
            }

            R.id.pickMi -> {
                val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pickImageActivityResultLauncher.launch(Intent(ACTION_PICK).apply { setDataAndType(Uri.parse(imageDir), "image/*") })
                true
            }

            R.id.chooserMi -> {
                startActivity(Intent(ACTION_CHOOSER).apply {
                    putExtra(EXTRA_TITLE, "Choose your favorite browser")
                    putExtra(EXTRA_INTENT, browserIntent())
                })
                startActivity(browserIntent());
                true
            }

            else -> {
                false
            }
        }
    }

    private fun browserIntent(): Intent {
        val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
        return Intent(ACTION_VIEW, url)
    }

    private fun callPhone(call:Boolean){
        startActivity(
            Intent(if(call) ACTION_CALL else ACTION_DIAL).apply {
                "tel:${activityMainBinding.parameterTv.text}".also {
                    data = Uri.parse(it)
                }
            }
        )
    }
}