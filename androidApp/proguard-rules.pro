# Lettrus ProGuard Rules

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Ktor
-keep class io.ktor.** { *; }

# Keep Koin
-keep class org.koin.** { *; }

# Keep serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep app models
-keep class com.lettrus.domain.model.** { *; }
