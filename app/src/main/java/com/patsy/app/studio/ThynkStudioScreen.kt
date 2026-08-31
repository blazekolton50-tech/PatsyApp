package com.patsy.app.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ThynkBlack = Color.Black
private val ThynkCharcoal = Color(0xFF202124)
private val ThynkPanel = Color(0xFF2A2B2E)
private val ThynkWhite = Color(0xFFF7F7F7)
private val ThynkMuted = Color(0xFFAAAAB0)
private val SaveGreen = Color(0xFF69D58C)
private val CancelRed = Color(0xFFFF6B6B)
private val ThynkRainbow = Brush.horizontalGradient(
    listOf(
        Color(0xFFFF6B35),
        Color(0xFFFFD447),
        Color(0xFF4CD964),
        Color(0xFF36A9FF),
        Color(0xFF9B59FF),
        Color(0xFFFF4FA3),
    ),
)

enum class StudioSection {
    HOME,
    CREATE_NEW,
    PROJECTS,
    TEMPLATES,
    BRAND_KIT,
    INSPIRATION,
    AI_IMAGE,
    AI_VIDEO,
    CAMERA,
    MEME,
    COLLAGE,
    DOCUMENT,
    MUSIC,
    EDITOR,
}

enum class StudioProviderAction {
    AI_IMAGE,
    AI_VIDEO_10_SECONDS,
    CUTOUT,
    ORIGINAL_MUSIC,
}

@Composable
fun ThynkStudioScreen(
    brandHeader: @Composable () -> Unit,
    assistantSlot: @Composable () -> Unit = {},
    videoSourceUri: String? = null,
    onImportMedia: () -> Unit = {},
    onSave: (StudioEditorState) -> Unit = {},
    onExport: (StudioEditorState) -> Unit = {},
    onProviderAction: (StudioProviderAction) -> Unit = {},
) {
    var section by remember { mutableStateOf(StudioSection.HOME) }
    var editorState by remember {
        mutableStateOf(
            StudioEditorState.image(
                widthPx = 1080,
                heightPx = 1350,
                projectName = "Untitled design",
            ),
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(ThynkBlack)
            .padding(horizontal = 14.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (section != StudioSection.HOME) {
                TextButton(onClick = { section = StudioSection.HOME }) {
                    Text("Back", color = ThynkWhite)
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                // The caller supplies the locked THyNK logo asset. This screen never redraws it.
                brandHeader()
            }
            if (section == StudioSection.EDITOR) {
                Button(
                    onClick = { onSave(editorState) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaveGreen,
                        contentColor = Color.Black,
                    ),
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (section) {
                StudioSection.HOME -> StudioHome(
                    open = { section = it },
                    assistantSlot = assistantSlot,
                )

                StudioSection.CREATE_NEW -> CreateNewStudioProject(
                    onStart = { mode ->
                        editorState = when (mode) {
                            StudioMode.IMAGE -> StudioEditorState.image(1080, 1350, "Untitled image")
                            StudioMode.DOCUMENT -> StudioEditorState.image(1240, 1754, "Untitled document").copy(mode = StudioMode.DOCUMENT)
                            StudioMode.MEME -> StudioEditorState.image(1080, 1080, "Untitled meme").copy(mode = StudioMode.MEME)
                            StudioMode.COLLAGE -> StudioEditorState.image(1080, 1350, "Untitled collage").copy(mode = StudioMode.COLLAGE)
                            StudioMode.VIDEO -> StudioEditorState.video(10_000, projectName = "Untitled video")
                            StudioMode.CAMERA -> StudioEditorState.video(10_000, projectName = "Camera project").copy(mode = StudioMode.CAMERA)
                        }
                        section = StudioSection.EDITOR
                    },
                    open = { section = it },
                )

                StudioSection.EDITOR -> StudioEditorWorkspace(
                    state = editorState,
                    sourceUri = videoSourceUri,
                    onStateChange = { editorState = it },
                    onImportMedia = onImportMedia,
                    onExport = { onExport(editorState) },
                    onProviderAction = onProviderAction,
                    assistantSlot = assistantSlot,
                )

                StudioSection.AI_IMAGE -> ProviderWorkspace(
                    title = "AI Image",
                    description = "Describe an original image. Generation starts only after a connected provider accepts the job.",
                    button = "Generate image",
                    onRun = { onProviderAction(StudioProviderAction.AI_IMAGE) },
                    assistantSlot = assistantSlot,
                )

                StudioSection.AI_VIDEO -> ProviderWorkspace(
                    title = "AI Video — 10 seconds",
                    description = "THyNK requests exactly 10 seconds. Progress, cancel, failure and provider-confirmed completion stay visible.",
                    button = "Generate 10s video",
                    onRun = { onProviderAction(StudioProviderAction.AI_VIDEO_10_SECONDS) },
                    assistantSlot = assistantSlot,
                )

                StudioSection.MUSIC -> ProviderWorkspace(
                    title = "Original Music",
                    description = "Create original clips only, then audition, trim, loop, fade and mix before attaching them to a project.",
                    button = "Create original clip",
                    onRun = { onProviderAction(StudioProviderAction.ORIGINAL_MUSIC) },
                    assistantSlot = assistantSlot,
                )

                StudioSection.TEMPLATES -> SimpleStudioLibrary(
                    title = "Templates",
                    subtitle = "Editable image, video, document, meme, collage, education, business, personal and marketing starters.",
                    items = listOf("Image templates", "Video templates", "Documents", "Memes", "Collages", "Education", "Business", "Personal"),
                )

                StudioSection.PROJECTS -> SimpleStudioLibrary(
                    title = "My Projects",
                    subtitle = "Recent, saved, locked and continuing work belongs here.",
                    items = listOf("Recent", "Images", "Videos", "Documents", "Saved", "Locked"),
                )

                StudioSection.BRAND_KIT -> SimpleStudioLibrary(
                    title = "Brand Kit",
                    subtitle = "Logos, approved colours, fonts and saved elements. Locked assets are never auto-rotated.",
                    items = listOf("Logos", "Colours", "Fonts", "Saved elements"),
                )

                StudioSection.INSPIRATION -> SimpleStudioLibrary(
                    title = "Inspiration",
                    subtitle = "Original prompts and starting ideas — never copied layouts.",
                    items = listOf("Fresh ideas", "Seasonal", "Social", "Education", "Business", "Personal"),
                )

                StudioSection.CAMERA -> SimpleStudioLibrary(
                    title = "Camera Studio",
                    subtitle = "Capture first, then open the result in the shared editor and timeline.",
                    items = listOf("Photo", "Video", "Recent captures", "Open editor"),
                )

                StudioSection.MEME -> SimpleStudioLibrary(
                    title = "Meme Studio",
                    subtitle = "Original layouts with editable image, text, stickers, frames and effects.",
                    items = listOf("Blank meme", "Square", "Portrait", "Landscape"),
                )

                StudioSection.COLLAGE -> SimpleStudioLibrary(
                    title = "Collage Studio",
                    subtitle = "Editable grids, freeform layouts, frames, spacing and backgrounds.",
                    items = listOf("2 photos", "3 photos", "4 photos", "Freeform", "Scrapbook"),
                )

                StudioSection.DOCUMENT -> SimpleStudioLibrary(
                    title = "Documents & Homework",
                    subtitle = "Flyers, guides, lists, worksheets and under-16 school-safe creation surfaces.",
                    items = listOf("Document", "Worksheet", "Homework", "Flyer", "Guide", "Checklist"),
                )
            }
        }
    }
}

@Composable
private fun StudioHome(
    open: (StudioSection) -> Unit,
    assistantSlot: @Composable () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Create. Design. Inspire.", color = ThynkWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        assistantSlot()
        StudioHomeCard("Create New", "Image, video, document, meme, collage or camera", { open(StudioSection.CREATE_NEW) })
        StudioHomeCard("My Projects", "Continue, organise and restore work", { open(StudioSection.PROJECTS) })
        StudioHomeCard("Templates", "Original editable starters", { open(StudioSection.TEMPLATES) })
        StudioHomeCard("AI Image", "Provider-backed original image generation", { open(StudioSection.AI_IMAGE) })
        StudioHomeCard("AI Video", "Exactly 10-second provider-backed generation", { open(StudioSection.AI_VIDEO) })
        StudioHomeCard("Camera Studio", "Capture and edit", { open(StudioSection.CAMERA) })
        StudioHomeCard("Original Music", "Create, trim, loop, fade and mix", { open(StudioSection.MUSIC) })
        StudioHomeCard("Brand Kit", "Approved logos, colours, fonts and elements", { open(StudioSection.BRAND_KIT) })
        StudioHomeCard("Inspiration", "Fresh original ideas", { open(StudioSection.INSPIRATION) })
    }
}

@Composable
private fun StudioHomeCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(ThynkCharcoal, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
    ) {
        Text(title, color = ThynkWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(subtitle, color = ThynkMuted, fontSize = 13.sp)
    }
}

@Composable
private fun CreateNewStudioProject(
    onStart: (StudioMode) -> Unit,
    open: (StudioSection) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("What would you like to create?", color = ThynkWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        CreationChoice("Image", "Editable canvas", { onStart(StudioMode.IMAGE) })
        CreationChoice("Video", "Player + multi-layer timeline", { onStart(StudioMode.VIDEO) })
        CreationChoice("Document", "Flyers, guides, lists & homework", { onStart(StudioMode.DOCUMENT) })
        CreationChoice("Meme", "Original editable meme layouts", { onStart(StudioMode.MEME) })
        CreationChoice("Collage", "Grid and freeform layouts", { onStart(StudioMode.COLLAGE) })
        CreationChoice("Camera", "Capture into the editor", { onStart(StudioMode.CAMERA) })
        CreationChoice("AI Image", "Real provider job", { open(StudioSection.AI_IMAGE) })
        CreationChoice("AI Video — 10s", "Real provider job", { open(StudioSection.AI_VIDEO) })
    }
}

@Composable
private fun CreationChoice(title: String, subtitle: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Text(title, color = ThynkWhite, fontWeight = FontWeight.Bold)
            Text(subtitle, color = ThynkMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StudioEditorWorkspace(
    state: StudioEditorState,
    sourceUri: String?,
    onStateChange: (StudioEditorState) -> Unit,
    onImportMedia: () -> Unit,
    onExport: () -> Unit,
    onProviderAction: (StudioProviderAction) -> Unit,
    assistantSlot: @Composable () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        assistantSlot()
        Text(state.projectName, color = ThynkWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(
            "${state.canvasWidthPx} × ${state.canvasHeightPx} • ${state.mode.name.lowercase()}",
            color = ThynkMuted,
            fontSize = 12.sp,
        )

        if (state.mode == StudioMode.VIDEO || state.mode == StudioMode.CAMERA) {
            StudioVideoPlayer(
                sourceUri = sourceUri,
                state = state,
                onAction = { action -> onStateChange(reduceStudioState(state, action)) },
            )
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFF101114), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("Canvas", color = ThynkMuted)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onImportMedia, modifier = Modifier.weight(1f)) { Text("Import") }
            Button(onClick = onExport, modifier = Modifier.weight(1f)) { Text("Export") }
        }

        StudioToolRail(
            state = state,
            onSelect = { descriptor ->
                when (descriptor.id) {
                    "ai" -> onProviderAction(StudioProviderAction.AI_IMAGE)
                    "cutout" -> onProviderAction(StudioProviderAction.CUTOUT)
                    "music" -> onProviderAction(StudioProviderAction.ORIGINAL_MUSIC)
                    "export" -> onExport()
                    else -> descriptor.toStudioTool()?.let { tool ->
                        onStateChange(reduceStudioState(state, StudioAction.SelectTool(tool)))
                    }
                }
            },
        )

        when (state.selectedTool) {
            StudioTool.FILTERS -> PresetStrip("Original Filters", listOf("Soft Dawn", "Clear Pop", "Moon Milk", "Rain Glass", "Golden Quiet", "Blue Hour Lift"))
            StudioTool.EFFECTS -> PresetStrip("Original Effects", listOf("Prism Edge", "Halo Drift", "Paper Dust", "Neon Trace", "Glass Bloom", "Ripple Lens"))
            StudioTool.FRAMES -> PresetStrip("Original Frames", listOf("Rainbow Whisper", "Ink Corner", "Floating Film", "Soft Window", "Wave Trim", "Glow Bracket"))
            StudioTool.ANIMATE -> PresetStrip("Original Motion", listOf("Gentle Rise", "Side Float", "Pop Settle", "Type Bloom", "Soft Bounce", "Orbit In"))
            else -> Unit
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) {
                Text("Cancel", color = CancelRed)
            }
            Button(
                onClick = { onExport() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ThynkWhite, contentColor = Color.Black),
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun StudioToolRail(
    state: StudioEditorState,
    onSelect: (StudioToolDescriptor) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        StudioToolCatalog.forMode(state.mode)
            .groupBy { it.group }
            .forEach { (group, tools) ->
                Text(group.name.lowercase().replaceFirstChar { it.uppercase() }, color = ThynkMuted, fontSize = 11.sp)
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    tools.forEach { tool ->
                        OutlinedButton(onClick = { onSelect(tool) }) {
                            Text(tool.label, color = ThynkWhite, fontSize = 12.sp)
                        }
                    }
                }
            }
    }
}

private fun StudioToolDescriptor.toStudioTool(): StudioTool? = when (id) {
    "templates" -> StudioTool.TEMPLATES
    "text" -> StudioTool.TEXT
    "media" -> StudioTool.MEDIA
    "elements", "stickers" -> StudioTool.ELEMENTS
    "draw", "eraser" -> StudioTool.DRAW
    "crop" -> StudioTool.CROP
    "cutout" -> StudioTool.CUTOUT
    "layers", "position", "opacity", "blend" -> StudioTool.LAYERS
    "frames" -> StudioTool.FRAMES
    "guides" -> StudioTool.GUIDES
    "filters" -> StudioTool.FILTERS
    "adjust" -> StudioTool.ADJUST
    "effects" -> StudioTool.EFFECTS
    "animate", "trim", "split", "speed", "transitions", "captions" -> StudioTool.ANIMATE
    "audio", "music", "volume", "fade" -> StudioTool.AUDIO
    "export" -> StudioTool.EXPORT
    else -> null
}

@Composable
private fun PresetStrip(title: String, items: List<String>) {
    Text(title, color = ThynkWhite, fontWeight = FontWeight.Bold)
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        items.forEach { item ->
            Box(
                Modifier
                    .width(120.dp)
                    .background(ThynkPanel, RoundedCornerShape(18.dp))
                    .padding(14.dp),
            ) {
                Text(item, color = ThynkWhite, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProviderWorkspace(
    title: String,
    description: String,
    button: String,
    onRun: () -> Unit,
    assistantSlot: @Composable () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        assistantSlot()
        Text(title, color = ThynkWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(description, color = ThynkMuted)
        Box(
            Modifier
                .fillMaxWidth()
                .background(ThynkCharcoal, RoundedCornerShape(22.dp))
                .padding(18.dp),
        ) {
            Text("Provider state will appear here: ready • queued • running • failed • cancelled • completed.", color = ThynkWhite)
        }
        Button(onClick = onRun, modifier = Modifier.fillMaxWidth()) { Text(button) }
        Text("THyNK never shows success until the connected provider confirms it.", color = ThynkMuted, fontSize = 12.sp)
    }
}

@Composable
private fun SimpleStudioLibrary(
    title: String,
    subtitle: String,
    items: List<String>,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, color = ThynkWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = ThynkMuted)
        items.forEach { item ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(ThynkCharcoal, RoundedCornerShape(20.dp))
                    .padding(18.dp),
            ) {
                Text(item, color = ThynkWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}
