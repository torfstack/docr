package com.torfstack.docr.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.torfstack.docr.auth.DocrAuth
import com.torfstack.docr.auth.DocrBiometrics
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView() {
    DocRTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Settings")
                    },
                )
            }
        ) {
            val context = LocalContext.current as FragmentActivity
            var biometricsToggle by remember { mutableStateOf(DocrAuth().isBiometricEnabled(context)) }
            var passwordToggle by remember { mutableStateOf(DocrAuth().isPasswordEnabled(context)) }
            var showPasswordDialog by remember { mutableStateOf(false) }
            LazyVerticalGrid(modifier = Modifier.padding(it), columns = GridCells.Fixed(1)) {
                item {
                    Setting(
                        title = "Password",
                        description = "Set a password",
                        onToggle = { activated ->
                            if (activated) {
                                showPasswordDialog = true
                            } else {
                                DocrAuth().setPasswordActivated(context, false)
                                passwordToggle = false
                            }
                        },
                        checked = passwordToggle
                    )
                }
                item {
                    Setting(
                        title = "Biometrics",
                        description = "Enable biometric authentication",
                        onToggle = { activated ->
                            if (activated) {
                                DocrBiometrics().authenticate(
                                    context,
                                    { DocrAuth().setBiometricsActivated(context, true) },
                                    {}
                                )
                            } else {
                                DocrAuth().setBiometricsActivated(context, false)
                            }
                            biometricsToggle = !biometricsToggle
                        },
                        checked = biometricsToggle
                    )
                }
            }
            if (showPasswordDialog) {
                DocrAuth().SetPassword(fragmentActivity = context) { isPasswordSet ->
                    showPasswordDialog = false
                    DocrAuth().setPasswordActivated(context, isPasswordSet)
                    passwordToggle = isPasswordSet
                }
            }
        }
    }
}

@Composable
fun Setting(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onToggle: (Boolean) -> Unit,
    checked: Boolean
) {
    Row(
        modifier = modifier.padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = title, style = Typography.titleMedium)
            Text(text = description, style = Typography.bodyMedium)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle(it) }
        )
    }
}