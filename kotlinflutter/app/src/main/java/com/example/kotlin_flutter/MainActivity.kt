daspackage com.example.kotlin_flutter

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel


class MainActivity : AppCompatActivity() {
    private val channelName = "foo"
    val engineID = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        //var flutterView = FlutterView(this);
        var flutterEngine = FlutterEngine(this);
        flutterEngine
            .dartExecutor
            .executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )

        FlutterEngineCache.getInstance().put(engineID, flutterEngine);

        MethodChannel(flutterEngine.getDartExecutor(), channelName).setMethodCallHandler { call, result ->
            when (call.method) {
                "bar" -> result.success("Koltin - Hello, ${call.arguments}")
                "baz" -> result.error("400", "This is bad", null)
                else -> result.notImplemented()
            }
        }

        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "foo")
        // Invoke a Dart method.
        val name = "bar" // or "baz", or "unknown"
        val value = "world"

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            channel.invokeMethod(name, value, object : MethodChannel.Result {
                override fun success(result: Any?) {
                    Log.i("MSG", "$result")
                }

                override fun error(code: String?, msg: String?, details: Any?) {
                    Log.e("MSG", "$name failed: $msg")
                }

                override fun notImplemented() {
                    Log.e("MSG", "$name not implemented")
                }
            })

//            startActivity(
//                FlutterActivity.createDefaultIntent(this)
//            )
            startActivity(FlutterActivity.withCachedEngine(engineID).build(this));
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}