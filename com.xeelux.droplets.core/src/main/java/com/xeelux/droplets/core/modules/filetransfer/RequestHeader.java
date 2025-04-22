package com.xeelux.droplets.core.modules.filetransfer;

import com.xeelux.droplets.core.text.JsonSerializable;

public interface RequestHeader extends JsonSerializable {
    ConnectionType getConnectionType();
    RequestHeader setConnectionType(final ConnectionType connectionType);


}
