package com.github.otsns.neyroCore;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class WrappedChatComponent {
    public static com.comphenix.protocol.wrappers.WrappedChatComponent fromText(String text) {
        return com.comphenix.protocol.wrappers.WrappedChatComponent.fromJson(
            ComponentSerializer.toString(TextComponent.fromLegacyText(text))
        );
    }
}
