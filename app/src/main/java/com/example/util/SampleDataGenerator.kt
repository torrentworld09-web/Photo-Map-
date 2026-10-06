package com.example.util

import com.example.data.local.FolderEntity
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.data.model.SyncState
import com.example.data.model.UserRole

object SampleDataGenerator {
    val sampleFolders = listOf(
        FolderEntity(
            id = "folder_1",
            name = "Land Survey",
            iconName = "folder",
            colorHex = "#F59E0B", // Yellow/Amber
            postCount = 24,
            storageBytes = 182L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 10,
            updatedAt = System.currentTimeMillis() - 86400000L * 1
        ),
        FolderEntity(
            id = "folder_2",
            name = "Nature",
            iconName = "folder",
            colorHex = "#10B981", // Green
            postCount = 48,
            storageBytes = 356L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 12,
            updatedAt = System.currentTimeMillis() - 86400000L * 2
        ),
        FolderEntity(
            id = "folder_3",
            name = "Travel",
            iconName = "folder",
            colorHex = "#3B82F6", // Blue
            postCount = 36,
            storageBytes = 214L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 15,
            updatedAt = System.currentTimeMillis() - 86400000L * 3
        ),
        FolderEntity(
            id = "folder_4",
            name = "Work",
            iconName = "folder",
            colorHex = "#F97316", // Orange
            postCount = 12,
            storageBytes = 98L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 20,
            updatedAt = System.currentTimeMillis() - 86400000L * 4
        ),
        FolderEntity(
            id = "folder_5",
            name = "Family",
            iconName = "folder",
            colorHex = "#EF4444", // Red
            postCount = 28,
            storageBytes = 176L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 25,
            updatedAt = System.currentTimeMillis() - 86400000L * 5
        ),
        FolderEntity(
            id = "folder_6",
            name = "Events",
            iconName = "folder",
            colorHex = "#8B5CF6", // Purple
            postCount = 16,
            storageBytes = 121L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 30,
            updatedAt = System.currentTimeMillis() - 86400000L * 6
        ),
        FolderEntity(
            id = "folder_7",
            name = "Documents",
            iconName = "folder",
            colorHex = "#6366F1", // Indigo
            postCount = 8,
            storageBytes = 54L * 1024 * 1024,
            createdAt = System.currentTimeMillis() - 86400000L * 40,
            updatedAt = System.currentTimeMillis() - 86400000L * 7
        )
    )

    val samplePosts = listOf(
        PostEntity(
            id = "BG-20260927-000001",
            title = "Mountain View",
            description = "Beautiful mountain landscape with lake and pristine alpine reflections",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=11.0168,76.9558",
            latitude = 11.0168,
            longitude = 76.9558,
            locationName = "Coimbatore, Tamil Nadu",
            plusCode = "8F6Q+4X Coimbatore",
            date = "27 Sep 2026",
            time = "06:30 AM",
            conditions = "Sunny",
            folderId = "folder_2",
            folderName = "Nature",
            isFavorite = true,
            createdAt = System.currentTimeMillis() - 3600000L * 2,
            updatedAt = System.currentTimeMillis() - 3600000L * 2,
            storageBytes = 4_200_000L,
            cloudFileId = "drive_file_001",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1426604966848-d7adac402bff?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Landscape", "Nature", "Mountains", "Lake"),
            isCameraPhoto = true,
            isImported = false,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260926-000002",
            title = "Beach Sunset",
            description = "Sunset at beach with golden hour ocean reflection",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=13.0827,80.2707",
            latitude = 13.0827,
            longitude = 80.2707,
            locationName = "Chennai, Tamil Nadu",
            plusCode = "7M52+8R Chennai",
            date = "26 Sep 2026",
            time = "05:45 PM",
            conditions = "Clear",
            folderId = "folder_3",
            folderName = "Travel",
            isFavorite = true,
            createdAt = System.currentTimeMillis() - 86400000L * 1,
            updatedAt = System.currentTimeMillis() - 86400000L * 1,
            storageBytes = 3_800_000L,
            cloudFileId = "drive_file_002",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Sunset", "Ocean", "Beach", "Travel"),
            isCameraPhoto = false,
            isImported = true,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260925-000003",
            title = "Forest Trail",
            description = "Nature walk through lush green pine woods and mist",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=11.4102,76.6950",
            latitude = 11.4102,
            longitude = 76.6950,
            locationName = "Ooty, Tamil Nadu",
            plusCode = "9P3W+HJ Ooty",
            date = "25 Sep 2026",
            time = "09:15 AM",
            conditions = "Misty",
            folderId = "folder_2",
            folderName = "Nature",
            isFavorite = false,
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            updatedAt = System.currentTimeMillis() - 86400000L * 2,
            storageBytes = 5_600_000L,
            cloudFileId = "drive_file_003",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=800&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1473448912268-2022ce9509d8?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Forest", "Trees", "Misty", "Trail"),
            isCameraPhoto = true,
            isImported = false,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260924-000004",
            title = "City Skyline",
            description = "City lights and modern architectural skyline at twilight",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=12.9716,77.5946",
            latitude = 12.9716,
            longitude = 77.5946,
            locationName = "Bengaluru, Karnataka",
            plusCode = "XH8J+9Q Bengaluru",
            date = "24 Sep 2026",
            time = "08:00 PM",
            conditions = "Night",
            folderId = "folder_4",
            folderName = "Work",
            isFavorite = true,
            createdAt = System.currentTimeMillis() - 86400000L * 3,
            updatedAt = System.currentTimeMillis() - 86400000L * 3,
            storageBytes = 4_100_000L,
            cloudFileId = "drive_file_004",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=800&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Skyline", "City", "Night", "Urban"),
            isCameraPhoto = false,
            isImported = true,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260927-000005",
            title = "Land Survey",
            description = "North side boundary photo with elevation marks and site perimeter",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=11.0168,76.9558",
            latitude = 11.0168,
            longitude = 76.9558,
            locationName = "Coimbatore, Tamil Nadu",
            plusCode = "8F6Q+4X Coimbatore",
            date = "27 Sep 2026",
            time = "06:30 AM",
            conditions = "Sunny",
            folderId = "folder_1",
            folderName = "Land Survey",
            isFavorite = false,
            createdAt = System.currentTimeMillis() - 3600000L * 5,
            updatedAt = System.currentTimeMillis() - 3600000L * 5,
            storageBytes = 4_800_000L,
            cloudFileId = "drive_file_005",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Survey", "Perimeter", "Field", "Plot"),
            isCameraPhoto = true,
            isImported = false,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260922-000006",
            title = "Family Gathering",
            description = "Weekend celebration with garden reunion and lunch",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=9.9252,78.1198",
            latitude = 9.9252,
            longitude = 78.1198,
            locationName = "Madurai, Tamil Nadu",
            plusCode = "6K23+LM Madurai",
            date = "22 Sep 2026",
            time = "12:30 PM",
            conditions = "Warm",
            folderId = "folder_5",
            folderName = "Family",
            isFavorite = true,
            createdAt = System.currentTimeMillis() - 86400000L * 5,
            updatedAt = System.currentTimeMillis() - 86400000L * 5,
            storageBytes = 6_200_000L,
            cloudFileId = "drive_file_006",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Family", "Celebration", "Garden"),
            isCameraPhoto = false,
            isImported = true,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260920-000007",
            title = "Annual Conference",
            description = "Tech keynote session and developer showcase",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=17.3850,78.4867",
            latitude = 17.3850,
            longitude = 78.4867,
            locationName = "Hyderabad, Telangana",
            plusCode = "CF98+2W Hyderabad",
            date = "20 Sep 2026",
            time = "11:00 AM",
            conditions = "Indoor",
            folderId = "folder_6",
            folderName = "Events",
            isFavorite = false,
            createdAt = System.currentTimeMillis() - 86400000L * 7,
            updatedAt = System.currentTimeMillis() - 86400000L * 7,
            storageBytes = 7_500_000L,
            cloudFileId = "drive_file_007",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Conference", "Tech", "Event"),
            isCameraPhoto = false,
            isImported = true,
            isDownloaded = true
        ),
        PostEntity(
            id = "BG-20260918-000008",
            title = "Property Deed Records",
            description = "Scanned documents and legal site registration records",
            mobile = "9876543210",
            mapsUrl = "https://maps.google.com/?q=11.0168,76.9558",
            latitude = 11.0168,
            longitude = 76.9558,
            locationName = "Coimbatore, Tamil Nadu",
            plusCode = "8F6Q+4X Coimbatore",
            date = "18 Sep 2026",
            time = "03:15 PM",
            conditions = "Clear",
            folderId = "folder_7",
            folderName = "Documents",
            isFavorite = false,
            createdAt = System.currentTimeMillis() - 86400000L * 9,
            updatedAt = System.currentTimeMillis() - 86400000L * 9,
            storageBytes = 3_200_000L,
            cloudFileId = "drive_file_008",
            syncStatus = SyncState.SYNCED,
            photoUris = listOf(
                "https://images.unsplash.com/photo-1450133064473-71024230f91b?w=800&auto=format&fit=crop"
            ),
            tags = listOf("Deed", "Legal", "Documents"),
            isCameraPhoto = false,
            isImported = false,
            isDownloaded = true
        )
    )

    val sampleUsers = listOf(
        UserEntity(
            id = "user_1",
            username = "user123",
            email = "user123@gmail.com",
            role = UserRole.USER,
            isActive = true,
            lastLogin = "2 hours ago",
            lastSync = "Today, 08:30 AM",
            postCount = 12,
            photoCount = 48,
            storageBytes = 2_400_000_000L
        ),
        UserEntity(
            id = "user_2",
            username = "photolover",
            email = "photolover@gmail.com",
            role = UserRole.USER,
            isActive = true,
            lastLogin = "4 hours ago",
            lastSync = "Today, 06:15 AM",
            postCount = 56,
            photoCount = 243,
            storageBytes = 8_600_000_000L
        ),
        UserEntity(
            id = "user_3",
            username = "travelGirl",
            email = "travelgirl@gmail.com",
            role = UserRole.USER,
            isActive = true,
            lastLogin = "6 hours ago",
            lastSync = "Yesterday, 09:20 PM",
            postCount = 34,
            photoCount = 120,
            storageBytes = 4_200_000_000L
        ),
        UserEntity(
            id = "user_4",
            username = "naturePro",
            email = "naturepro@gmail.com",
            role = UserRole.USER,
            isActive = true,
            lastLogin = "1 day ago",
            lastSync = "Yesterday, 04:00 PM",
            postCount = 28,
            photoCount = 96,
            storageBytes = 3_800_000_000L
        ),
        UserEntity(
            id = "user_5",
            username = "workUser",
            email = "workuser@gmail.com",
            role = UserRole.USER,
            isActive = false,
            lastLogin = "3 days ago",
            lastSync = "3 days ago",
            postCount = 12,
            photoCount = 32,
            storageBytes = 1_100_000_000L
        )
    )
}
