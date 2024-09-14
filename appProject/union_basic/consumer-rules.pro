-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keep class com.luck.picture.lib.** { *; }

#//保持 com.union.union_basic.network.BaseResultBean的成员变量不被重命名
-keepclassmembernames class com.union.union_basic.network.BaseResultBean{ <fields>;}

#如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }

#如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# 保留R下面的资源
-keep class **.R$* {
 *;
}