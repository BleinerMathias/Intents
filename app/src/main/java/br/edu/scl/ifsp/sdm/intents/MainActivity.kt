package br.edu.scl.ifsp.sdm.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
                true
            }
            R.id.viewMi -> {
                true
            }

            R.id.callMi -> {
                true
            }

            R.id.dialMi -> {
                true
            }

            R.id.pickMi -> {
                true
            }

            R.id.chooserMi -> {
                true
            }

            else -> {
                false
            }
        }
    }
}