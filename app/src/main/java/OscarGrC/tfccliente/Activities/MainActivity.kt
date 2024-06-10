package OscarGrC.tfccliente.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import OscarGrC.tfccliente.R
import OscarGrC.tfccliente.databinding.ActivityMainBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavMenu()
    }
    private fun initNavMenu() {
        val navHost = supportFragmentManager.findFragmentById(R.id.NavHostFragment) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}