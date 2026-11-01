# DM ADMIN — Android starter project

এটি DM ADMIN-এর প্রথম Android source scaffold। এতে native Android/Jetpack Compose UI, লাইসেন্সের মডেল এবং Supabase schema-এর প্রাথমিক খসড়া আছে।

## গুরুত্বপূর্ণ অবস্থা
- এটি এখনো installable APK নয়।
- Android SDK/Gradle build environment ছাড়া এখানে APK compile/sign করা যায়নি।
- UI-তে থাকা key list এখন demo/local state; Supabase API integration এখনো যুক্ত হয়নি।
- Supabase service-role key কখনো Android APK-তে রাখা যাবে না।
- মূল APK-তে পুরোনো Firebase/DPMods/AtheryX references পাওয়া গেছে; সেগুলো প্রতিস্থাপনের আগে মূল অ্যাপের সম্পূর্ণ validation flow শনাক্ত ও পরীক্ষা করতে হবে।

## Build
Android Studio-তে project folder খুলুন। Gradle sync করে Android SDK 35 ইনস্টল করুন, তারপর app run/build করুন।

## Backend
`supabase/schema.sql` হলো প্রাথমিক schema। Production-এ RLS enabled রাখতে হবে এবং privileged create/edit/ban/delete/validate কাজ Edge Function দিয়ে করতে হবে। Admin authentication ছাড়া public write access দেবেন না।

## ফোন থেকেই APK Build (Android Studio ছাড়াই)
এই ZIP-এ `.github/workflows/build-apk.yml` দেওয়া আছে। GitHub-এ project source আপলোড করে Actions চালালে GitHub-এর cloud runner APK build করবে এবং artifact হিসেবে `app-debug.apk` দেবে। Build workflow যোগ করা হয়েছে, কিন্তু এই কথোপকথন থেকে তোমার GitHub account-এ project আপলোড বা workflow চালানো হয়নি।
