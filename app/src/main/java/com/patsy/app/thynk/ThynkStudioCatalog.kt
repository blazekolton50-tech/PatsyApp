package com.patsy.app.thynk

data class ThynkCategory(
    val id: String,
    val label: String,
    val description: String,
    val items: List<String>,
)

data class ThynkMusicPage(
    val id: String,
    val title: String,
    val subtitle: String,
    val providerState: String = "LOCAL_UI",
)

object ThynkStudioCatalog {
    val categories = listOf(
        ThynkCategory(
            "design",
            "DESIGN & TEMPLATES",
            "Create graphics, layouts and editable designs.",
            listOf("POSTERS", "FLYERS", "INVITATIONS", "CARDS", "MENUS", "PRICE LISTS", "SIGNS", "CERTIFICATES", "BROCHURES", "LABELS", "BLANK DESIGNS", "CUSTOM SIZE", "TEMPLATES"),
        ),
        ThynkCategory(
            "social",
            "SOCIAL & CONTENT",
            "Build posts, stories and creator content.",
            listOf("SQUARE POST", "PORTRAIT / STORY", "CAROUSEL", "QUOTE", "MEME", "ANNOUNCEMENT", "BEFORE / AFTER", "CREATOR TIP", "STAT CARD", "INFOGRAPHIC", "THUMBNAIL", "BANNER", "PROMO GRAPHIC"),
        ),
        ThynkCategory(
            "photo",
            "PHOTO & IMAGE",
            "Edit, enhance and transform images.",
            listOf("EDIT PHOTO", "CROP", "RESIZE", "ROTATE", "FLIP", "CUTOUT", "REMOVE BACKGROUND", "REMOVE OBJECT", "FILTERS", "RETOUCH", "COLOR CORRECTION", "FRAMES"),
        ),
        ThynkCategory(
            "video",
            "VIDEO & CAMERA",
            "Shoot, edit and produce video.",
            listOf("VIDEO EDITOR", "TRIM & CUT", "SPLIT", "TRANSITIONS", "SUBTITLES", "OVERLAYS", "GREEN SCREEN", "VIDEO FILTERS", "SLOW MOTION", "SLIDESHOW", "LOOP", "ASPECT RESIZER"),
        ),
        ThynkCategory(
            "documents",
            "DOCUMENTS & BUSINESS",
            "Build useful documents and business materials.",
            listOf("DOCUMENT", "CV / RESUME", "INVOICE", "PROPOSAL", "LETTER", "REPORT", "AGENDA", "CHECKLIST", "BRAND GUIDE", "TIMELINE", "PRICE LIST", "BUSINESS PLAN"),
        ),
        ThynkCategory(
            "study",
            "HOMEWORK & STUDY",
            "Study, revise, organise and learn.",
            listOf("ESSAY HELP", "FLASHCARDS", "TIMETABLE", "RESEARCH PLAN", "CITATIONS", "LAB REPORT", "MIND MAP", "NOTES", "QUIZ MAKER", "REVISION CARDS", "PROJECT PLAN", "WORKSHEET"),
        ),
        ThynkCategory(
            "presentations",
            "PRESENTATIONS & PLANNING",
            "Present ideas and organise projects.",
            listOf("PRESENTATION", "PITCH DECK", "LESSON PLAN", "MOOD BOARD", "ROADMAP", "MEETING DECK", "PORTFOLIO", "SWOT ANALYSIS", "CALENDAR", "GOAL TRACKER", "KANBAN BOARD", "WEEKLY PLANNER"),
        ),
        ThynkCategory(
            "collage",
            "COLLAGE & CREATIVE",
            "Play, experiment and build creative layouts.",
            listOf("PHOTO COLLAGE", "SCRAPBOOK", "MOOD COLLAGE", "POLAROID WALL", "MEME BUILDER", "STICKER COLLAGE", "MAGAZINE CUTOUT", "GRID COLLAGE", "FREESTYLE BOARD", "SHAPE COLLAGE", "LAYERED ART", "MEMORY WALL"),
        ),
        ThynkCategory(
            "music",
            "THyNK MUSIC",
            "Create, record, edit, mix and master music.",
            listOf("CREATE MUSIC", "AI MUSIC GENERATOR", "RECORD", "TRACK EDITOR", "MIXER", "MASTERING", "LOOPS & SAMPLES", "STEMS", "SOUND EFFECTS", "LYRICS & VOCALS", "IMPORT AUDIO", "MY MUSIC"),
        ),
        ThynkCategory(
            "ai",
            "AI & MY STUDIO",
            "AI tools, projects, uploads and your creative space.",
            listOf("AI IMAGE", "AI WRITER", "AI BACKGROUND", "AI UPSCALE", "AI IDEAS", "MY PROJECTS", "MY FAVORITES", "MY UPLOADS", "MY FONTS", "MY PALETTES", "RECENT EDITS", "ARCHIVE"),
        ),
    )
}

object ThynkMusicCatalog {
    val pages = listOf(
        ThynkMusicPage("music-home", "THyNK Music", "Create. Record. Edit. Mix. Master."),
        ThynkMusicPage("create-music", "Create Music", "Start a new original track.", "NOT_CONFIGURED"),
        ThynkMusicPage("ai-music-generator", "AI Music Generator", "Generate original music through a configured provider.", "NOT_CONFIGURED"),
        ThynkMusicPage("track-editor", "Track Editor", "Arrange, trim and shape tracks."),
        ThynkMusicPage("mixer", "Mixer", "Balance tracks, pan and levels."),
        ThynkMusicPage("equalizer", "Equalizer", "Shape frequency balance."),
        ThynkMusicPage("effects", "Effects", "Apply local effect settings and prepare processing."),
        ThynkMusicPage("lyrics-vocals", "Lyrics & Vocals", "Write and organise lyrics and vocal ideas.", "NOT_CONFIGURED"),
        ThynkMusicPage("mastering", "Mastering", "Finalize through a configured mastering provider.", "NOT_CONFIGURED"),
        ThynkMusicPage("export", "Export", "Choose output settings; real file export must be verified.", "NOT_CONFIGURED"),
    )
}
