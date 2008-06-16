/*
 * Copyright (c) 2008 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pivot.wtk.skin.terra;

import pivot.wtk.Component;
import pivot.wtk.Dialog;
import pivot.wtk.Keyboard;

public class DialogSkin extends AbstractFrameSkin {
    private static final boolean DEFAULT_SHOW_MINIMIZE_BUTTON = false;
    private static final boolean DEFAULT_SHOW_MAXIMIZE_BUTTON = false;

    public DialogSkin() {
    }

    @Override
    public void install(Component component) {
        validateComponentType(component, Dialog.class);

        super.install(component);

        minimizeButton.setDisplayable(DEFAULT_SHOW_MINIMIZE_BUTTON);
        maximizeButton.setDisplayable(DEFAULT_SHOW_MAXIMIZE_BUTTON);
    }

    @Override
    public Object remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        Object previousValue = null;

        if (key.equals(SHOW_MINIMIZE_BUTTON_KEY)) {
            previousValue = put(key, DEFAULT_SHOW_MINIMIZE_BUTTON);
        } else if (key.equals(SHOW_MAXIMIZE_BUTTON_KEY)) {
            previousValue = put(key, DEFAULT_SHOW_MAXIMIZE_BUTTON);
        } else {
            previousValue = super.remove(key);
        }

        return previousValue;
    }

    @Override
    public boolean keyPressed(int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = false;

        Dialog dialog = (Dialog)getComponent();

        if (keyCode == Keyboard.KeyCode.ENTER) {
            dialog.close(true);
            consumed = true;
        } else if (keyCode == Keyboard.KeyCode.ESCAPE) {
            dialog.close(false);
            consumed = true;
        } else {
            consumed = super.keyPressed(keyCode, keyLocation);
        }

        return consumed;
    }
}
