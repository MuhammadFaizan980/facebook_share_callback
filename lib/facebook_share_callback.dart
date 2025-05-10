import 'dart:nativewrappers/_internal/vm/lib/typed_data_patch.dart';

import 'facebook_share_callback_platform_interface.dart';

enum ShareType { shareLinksFacebook, sharePhotoFacebook, more }

class FacebookShareCallback {
  Future<String?> shareFacebook({
    required ShareType type,
    String? quote,
    String? url,
    Uint8List? uint8Image,
    String? imageName,
  }) {
    return FacebookShareCallbackPlatform.instance.shareFacebook(
      type: type.toString(),
      quote: quote,
      url: url,
      uint8Image: uint8Image,
      imageName: imageName,
    );
  }
}
