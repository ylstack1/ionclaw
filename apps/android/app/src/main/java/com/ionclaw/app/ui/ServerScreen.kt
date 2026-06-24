package com.ionclaw.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionclaw.app.R
import com.ionclaw.app.server.ServerViewModel
import com.ionclaw.app.ui.theme.BrandPrimary
import com.ionclaw.app.ui.theme.BrandSuccess
import com.ionclaw.app.ui.theme.BrandDanger
import com.ionclaw.app.ui.theme.CardBorder
import com.ionclaw.app.ui.theme.CardSurface
import com.ionclaw.app.ui.theme.HeaderBackground
import com.ionclaw.app.ui.theme.ScreenBackground
import com.ionclaw.app.ui.theme.TextOnHeader
import com.ionclaw.app.ui.theme.TextPrimary
import com.ionclaw.app.ui.theme.TextSecondary

private val ButtonShape = RoundedCornerShape(12.dp)
private val CardShape = RoundedCornerShape(20.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerScreen(viewModel: ServerViewModel, onOpenPanel: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        containerColor = ScreenBackground,
        topBar = {
            Surface(shadowElevation = 4.dp) {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(R.drawable.logo_dark),
                            contentDescription = "IonClaw",
                            modifier = Modifier.height(32.dp)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = HeaderBackground,
                        titleContentColor = TextOnHeader
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ServerStatusView(isRunning = viewModel.isRunning)

            ServerCard(viewModel)

            ActionButtons(viewModel, onOpenPanel)

            if (viewModel.isRunning && viewModel.addresses.isNotEmpty()) {
                NetworkCard(addresses = viewModel.addresses, port = viewModel.port)
            }

            viewModel.lastError?.let { error ->
                Surface(
                    color = BrandDanger.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandDanger,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ServerCard(viewModel: ServerViewModel) {
    val config = viewModel.config
    val fieldsEnabled = !viewModel.isRunning && !viewModel.isBusy

    SectionCard(
        title = "Connection Settings",
        icon = { Icon(Icons.Outlined.Dns, null, tint = BrandPrimary, modifier = Modifier.size(20.dp)) },
        iconTint = BrandPrimary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = config.host,
                onValueChange = { viewModel.updateConfig(it, config.port) },
                label = { Text("Server Host") },
                placeholder = { Text("0.0.0.0") },
                singleLine = true,
                enabled = fieldsEnabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = CardBorder
                )
            )

            OutlinedTextField(
                value = config.port.toString(),
                onValueChange = { input ->
                    viewModel.updateConfig(config.host, input.filter(Char::isDigit).take(5).toIntOrNull() ?: 0)
                },
                label = { Text("Server Port") },
                placeholder = { Text("8080") },
                singleLine = true,
                enabled = fieldsEnabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = CardBorder
                )
            )
        }
    }
}

@Composable
private fun ActionButtons(viewModel: ServerViewModel, onOpenPanel: () -> Unit) {
    if (viewModel.isBusy) {
        Box(modifier = Modifier.height(120.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = BrandPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (viewModel.isRunning) {
            PrimaryAction("Open Control Panel", Icons.Outlined.Language, BrandPrimary, onOpenPanel)
            SecondaryAction("Stop Server", Icons.Filled.Stop, BrandDanger, viewModel::stop)
        } else {
            PrimaryAction("Start Server", Icons.Filled.PlayArrow, BrandPrimary, viewModel::start)
            SecondaryAction("Initialize Project", Icons.Outlined.FolderOpen, TextSecondary, viewModel::initializeProject)
        }
    }
}

@Composable
private fun PrimaryAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SecondaryAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = ButtonShape,
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NetworkCard(addresses: List<String>, port: Int) {
    val context = LocalContext.current
    var copiedAddress by remember { mutableStateOf<String?>(null) }

    SectionCard(
        title = "Access URLs",
        icon = { Icon(Icons.Outlined.Wifi, null, tint = BrandSuccess, modifier = Modifier.size(20.dp)) },
        iconTint = BrandSuccess
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            addresses.forEach { address ->
                val url = "http://$address:$port"
                val copied = copiedAddress == address

                Surface(
                    onClick = {
                        copyToClipboard(context, url)
                        copiedAddress = address
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = ScreenBackground.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (copied) BrandSuccess.copy(alpha = 0.5f) else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (address == "127.0.0.1" || address == "localhost") "Local Access" else "Network Access",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                            Text(
                                text = url,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }

                        Icon(
                            imageVector = if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = if (copied) BrandSuccess else BrandPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Text(
                text = "Tap an address to copy it to clipboard.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, icon: @Composable () -> Unit, iconTint: Color, content: @Composable () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.outlinedCardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Box(modifier = Modifier.padding(top = 24.dp)) {
                content()
            }
        }
    }
}

private fun copyToClipboard(context: Context, value: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("IonClaw", value))
}
