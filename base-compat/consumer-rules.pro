## OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

## Gson
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Application classes that will be serialized/deserialized over Gson
-keep class org.kin.stellarfork.responses.** { <fields>; }

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Ignore usages of x509 in eddsa library as it's not in use in our case
-dontwarn sun.security.x509.**

-dontwarn io.envoyproxy.**
-dontwarn org.kin.shaded.**
-dontwarn org.kin.sdk.**
-dontwarn java.lang.ClassValue

# definitely need
-keep class org.kin.sdk.base.network.services.KinService.*$* {*;}
-keep class org.kin.agora.gen.** {*;}
-keep class org.kin.agora.gen.**$* {*;}
-keep class org.kin.agora.gen.account.v3.AccountService {*;}

# ProtobufLite - https://github.com/protocolbuffers/protobuf/blob/543817295e05bcf226628ec26706e575741a0722/java/lite.md
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

## Trying to avoid these below by refining further in the future...
# For normal builds
-keep class org.kin.*$* {*;}
-keep class org.kin.**$* {*;}
-keep class io.grpc.** {*;}
-keep class org.kin.** {*;}

# For shaded builds
-keep class org.kin.shaded.** {*;}
-keep class org.kin.shaded.*$* {*;}
