package com.dmadmin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneOffset

private val Bg = Color(0xFF08080B)
private val Panel = Color(0xFF15151B)
private val Purple = Color(0xFF8B5CF6)
private val Muted = Color(0xFF9CA3AF)

data class License(
    val key: String,
    val alias: String,
    val deviceLimit: Int,
    val expiry: LocalDate,
    val status: String = "ACTIVE"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DmAdminApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmAdminApp() {
    var licenses by remember {
        mutableStateOf(listOf(
            License("AD-VIP-93XDQ7", "Amjad", 1, LocalDate.of(2026, 10, 31), "EXPIRED"),
            License("AD-VIP-AD9NFH", "Free", 2, LocalDate.of(2026, 11, 2), "BANNED"),
            License("AD-VIP-BFNU88", "Me", 1, LocalDate.of(2026, 11, 2))
        ))
    }
    var editing by remember { mutableStateOf<License?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("ALL") }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Purple,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Bg,
            topBar = {
                Row(Modifier.fillMaxWidth().background(Color(0xFF111018)).padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, null, tint = Purple)
                    Spacer(Modifier.width(10.dp))
                    Text("DM ADMIN", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text("鈼� LIVE", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }, containerColor = Purple) {
                    Icon(Icons.Default.Add, contentDescription = "Create license")
                }
            },
            bottomBar = {
                Row(Modifier.fillMaxWidth().background(Color(0xFF0D0915)).padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("馃攽  Keys", color = Purple, fontWeight = FontWeight.Bold)
                    Text("鈿�  Settings", color = Muted)
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text("License Keys", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL", "ACTIVE", "EXPIRED", "BANNED").forEach { f ->
                        FilterChip(selected = filter == f, onClick = { filter = f }, label = { Text(f) })
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(licenses.filter { filter == "ALL" || it.status == filter }) { item ->
                        LicenseCard(item,
                            onEdit = { editing = item },
                            onBan = {
                                licenses = licenses.map { if (it.key == item.key) it.copy(status = if (it.status == "BANNED") "ACTIVE" else "BANNED") else it }
                            },
                            onDelete = { licenses = licenses.filterNot { it.key == item.key } }
                        )
                    }
                }
            }
        }
        if (editing != null) {
            LicenseEditor(editing!!, onDismiss = { editing = null }, onSave = { updated ->
                licenses = licenses.map { if (it.key == updated.key) updated else it }
                editing = null
            })
        }
        if (showCreate) {
            val fresh = License("AD-VIP-${(100000..999999).random()}", "", 1, LocalDate.now().plusDays(30))
            LicenseEditor(fresh, isNew = true, onDismiss = { showCreate = false }, onSave = {
                licenses = listOf(it) + licenses
                showCreate = false
            })
        }
    }
}

@Composable
private fun LicenseCard(item: License, onEdit: () -> Unit, onBan: () -> Unit, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.key, color = Purple, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(item.status, color = when(item.status) {
                    "ACTIVE" -> Color(0xFF10B981)
                    "EXPIRED" -> Color(0xFFEF4444)
                    else -> Color(0xFFF87171)
                }, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("馃懁 ${item.alias.ifBlank { "No alias" }}     鈻� ${item.expiry}     鈻� ${item.deviceLimit}", color = Muted)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Edit") }
                OutlinedButton(onClick = onBan, modifier = Modifier.weight(1f)) { Text(if (item.status == "BANNED") "Unban" else "Ban") }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Text("Delete", color = Color(0xFFEF4444)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LicenseEditor(
    initial: License,
    isNew: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (License) -> Unit
) {
    var key by remember { mutableStateOf(initial.key) }
    var alias by remember { mutableStateOf(initial.alias) }
    var device by remember { mutableStateOf(initial.deviceLimit.toString()) }
    var expiry by remember { mutableStateOf(initial.expiry.toString()) }
    var showCalendar by remember { mutableStateOf(false) }
    val calendarState = rememberDatePickerState(initialSelectedDateMillis = initial.expiry.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111016),
        title = { Text(if (isNew) "Create License Key" else "Edit License Key", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(key, { key = it.uppercase() }, label = { Text("License Key") }, singleLine = true)
                OutlinedTextField(alias, { alias = it }, label = { Text("Username / Alias") }, singleLine = true)
                OutlinedTextField(device, { device = it.filter(Char::isDigit) }, label = { Text("Device Limit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(expiry, { expiry = it }, label = { Text("Expiry Date (YYYY-MM-DD)") }, singleLine = true,
                    readOnly = true, trailingIcon = { TextButton(onClick = { showCalendar = true }) { Text("馃搮") } })
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 7, 30).forEach { days ->
                        TextButton(onClick = { expiry = LocalDate.now().plusDays(days.toLong()).toString() }) {
                            Text("${days}D")
                        }
                    }
                }
                Text("唳曕唳唳侧唳ㄠ唳∴唳� 唳ム唳曕 唳む唳班唳� 唳唳涏 唳ㄠ唳� 唳呧Ε唳 1D / 7D / 30D 唳氞唳唳ㄠイ", color = Muted, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                val parsed = runCatching { LocalDate.parse(expiry) }.getOrNull()
                val count = device.toIntOrNull()
                if (key.startsWith("AD") && parsed != null && count != null && count > 0) {
                    onSave(initial.copy(key = key, alias = alias, deviceLimit = count, expiry = parsed))
                }
            }) { Text("SAVE KEY") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } }
    )
    if (showCalendar) {
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    calendarState.selectedDateMillis?.let {
                        expiry = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString()
                    }
                    showCalendar = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showCalendar = false }) { Text("CANCEL") } }
        ) { DatePicker(state = calendarState) }
    }
}
