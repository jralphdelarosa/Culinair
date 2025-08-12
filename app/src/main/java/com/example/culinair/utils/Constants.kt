package com.example.culinair.utils

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */
object Constants {
    const val SUPABASE_URL = "https://voygyldtkkbwdljnfwfg.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZveWd5bGR0a2tid2Rsam5md2ZnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTMxMDc3OTksImV4cCI6MjA2ODY4Mzc5OX0.HIqFvoWM6aGBXEiAed-D13N8xFKCp9M59yuZ0MofUFI"

    const val WEB_CLIENT_ID = "1090286795701-9a5ke4uh7d6e4fl28d1ola0pego6e3pf.apps.googleusercontent.com"
    // Derived URLs
    const val SUPABASE_AUTH_URL = "$SUPABASE_URL/auth/v1"
    const val SUPABASE_REST_URL = "$SUPABASE_URL/rest/v1"
    const val SUPABASE_STORAGE_URL = "$SUPABASE_URL/storage/v1"
}