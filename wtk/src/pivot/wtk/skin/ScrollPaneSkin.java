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
package pivot.wtk.skin;

import pivot.wtk.Component;
import pivot.wtk.Dimensions;
import pivot.wtk.Keyboard;
import pivot.wtk.Mouse;
import pivot.wtk.Orientation;
import pivot.wtk.Rectangle;
import pivot.wtk.ScrollBar;
import pivot.wtk.ScrollBarValueListener;
import pivot.wtk.ScrollPane;
import pivot.wtk.ScrollPane.ScrollBarPolicy;
import pivot.wtk.ScrollPaneListener;
import pivot.wtk.Viewport;
import pivot.wtk.ViewportListener;

/**
 * Note that ScrollPaneSkin is a member of pivot.wtk.skin, not
 * pivot.wtk.skin.terra. This is because the fundamental behavior of the skin
 * should not change across themes, though the appearance of a scroll bar may.
 *
 * TODO add support for corner component
 *
 * TODO add components in the other three corners to prevent the view from
 * showing through in the corners. Do the same for the top left corner if the
 * scroll pane doesn't have a "corner component".
 *
 * @author tvolkert
 */
public class ScrollPaneSkin extends ContainerSkin
    implements Viewport.Skin, ScrollPaneListener, ViewportListener,
               ScrollBarValueListener {

    private ScrollBar horizontalScrollBar = new ScrollBar(Orientation.HORIZONTAL);
    private ScrollBar verticalScrollBar = new ScrollBar(Orientation.VERTICAL);

    // Style properties
    protected int horizontalReveal = DEFAULT_HORIZONTAL_REVEAL;
    protected int verticalReveal = DEFAULT_VERTICAL_REVEAL;

    // Default style values
    private static final int DEFAULT_HORIZONTAL_INCREMENT = 10;
    private static final int DEFAULT_VERTICAL_INCREMENT = 10;
    private static final int DEFAULT_HORIZONTAL_REVEAL = 30;
    private static final int DEFAULT_VERTICAL_REVEAL = 30;
    private static final float DEFAULT_SCROLL_BAR_OPACITY = 0.75f;

    // Style keys
    protected static final String HORIZONTAL_INCREMENT_KEY = "horizontalIncrement";
    protected static final String VERTICAL_INCREMENT_KEY = "verticalIncrement";
    protected static final String HORIZONTAL_REVEAL_KEY = "horizontalReveal";
    protected static final String VERTICAL_REVEAL_KEY = "verticalReveal";
    protected static final String SCROLL_BAR_OPACITY_KEY = "scrollBarOpacity";

    public ScrollPaneSkin() {
        super();

        horizontalScrollBar.setUnitIncrement(DEFAULT_HORIZONTAL_INCREMENT);
        verticalScrollBar.setUnitIncrement(DEFAULT_VERTICAL_INCREMENT);

        horizontalScrollBar.getStyles().put("backgroundOpacity",
            DEFAULT_SCROLL_BAR_OPACITY);
        verticalScrollBar.getStyles().put("backgroundOpacity",
            DEFAULT_SCROLL_BAR_OPACITY);
    }

    @Override
    public void install(Component component) {
        validateComponentType(component, ScrollPane.class);

        super.install(component);

        ScrollPane scrollPane = (ScrollPane)component;
        scrollPane.getViewportListeners().add(this);
        scrollPane.getScrollPaneListeners().add(this);

        scrollPane.getComponents().add(horizontalScrollBar);
        scrollPane.getComponents().add(verticalScrollBar);

        horizontalScrollBar.getScrollBarValueListeners().add(this);
        verticalScrollBar.getScrollBarValueListeners().add(this);
    }

    @Override
    public void uninstall() {
        ScrollPane scrollPane = (ScrollPane)getComponent();
        scrollPane.getViewportListeners().remove(this);
        scrollPane.getScrollPaneListeners().remove(this);

        scrollPane.getComponents().remove(horizontalScrollBar);
        scrollPane.getComponents().remove(verticalScrollBar);

        horizontalScrollBar.getScrollBarValueListeners().remove(this);
        verticalScrollBar.getScrollBarValueListeners().remove(this);

        super.uninstall();
    }

    @Override
    public int getPreferredWidth(int height) {
        int preferredWidth = 0;

        ScrollPane scrollPane = (ScrollPane)getComponent();
        Component view = scrollPane.getView();

        if (view != null) {
            int preferredRowHeaderWidth = 0;
            Component rowHeader = scrollPane.getRowHeader();
            if (rowHeader != null) {
                preferredRowHeaderWidth = rowHeader.getPreferredWidth(-1);
            }

            int preferredColumnHeaderHeight = 0;
            Component columnHeader = scrollPane.getColumnHeader();
            if (columnHeader != null) {
                preferredColumnHeaderHeight = columnHeader.getPreferredHeight(-1);
            }

            ScrollBarPolicy verticalPolicy = scrollPane.getVerticalPolicy();

            if (verticalPolicy != ScrollBarPolicy.FILL) {
                // Get the unconstrained preferred size of the view
                Dimensions preferredViewSize = view.getPreferredSize();

                // If the policy is FILL_TO_CAPACITY, and the sum of the
                // unconstrained preferred heights of the view and the column
                // header is less than the height constraint, apply the FILL
                // policy; otherwise, apply the AUTO policy

                if (verticalPolicy == ScrollBarPolicy.FILL_TO_CAPACITY) {
                    if (height < 0) {
                        verticalPolicy = ScrollBarPolicy.AUTO;
                    } else {
                        int preferredHeight = preferredViewSize.height +
                            preferredColumnHeaderHeight;

                        if (preferredHeight < height) {
                            verticalPolicy = ScrollBarPolicy.FILL;
                        } else {
                            verticalPolicy = ScrollBarPolicy.AUTO;
                        }
                    }
                }

                // If the policy is ALWAYS, NEVER, or AUTO, the preferred
                // width is the sum of the unconstrained preferred widths of
                // the view and row header, plus the width of the scroll
                // bar if policy is ALWAYS or if the view's preferred height is
                // greater than the height constraint and the policy is AUTO

                if (verticalPolicy == ScrollBarPolicy.ALWAYS
                    || verticalPolicy == ScrollBarPolicy.NEVER
                    || verticalPolicy == ScrollBarPolicy.AUTO) {
                    preferredWidth = preferredViewSize.width +
                        preferredRowHeaderWidth;

                    // If the sum of the preferred heights of the view and the
                    // column header is greater than the height constraint,
                    // include the preferred width of the scroll bar in the
                    // preferred width calculation
                    if (verticalPolicy == ScrollBarPolicy.ALWAYS
                        || (verticalPolicy == ScrollBarPolicy.AUTO
                        && height > 0
                        && preferredViewSize.height + preferredColumnHeaderHeight > height)) {
                        preferredWidth += verticalScrollBar.getPreferredWidth(-1);
                    }
                }
            }

            if (verticalPolicy == ScrollBarPolicy.FILL) {
                // Preferred width is the sum of the constrained preferred
                // width of the view and the unconstrained preferred width of
                // the row header

                if (height >= 0) {
                    // Subtract the unconstrained preferred height of the
                    // column header from the height constraint
                    height = Math.max(height - preferredColumnHeaderHeight, 0);
                }

                preferredWidth = view.getPreferredWidth(height) +
                    preferredRowHeaderWidth;
            }
        }

        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        int preferredHeight = 0;

        ScrollPane scrollPane = (ScrollPane)getComponent();
        Component view = scrollPane.getView();

        if (view != null) {
            int preferredRowHeaderWidth = 0;
            Component rowHeader = scrollPane.getRowHeader();
            if (rowHeader != null) {
                preferredRowHeaderWidth = rowHeader.getPreferredWidth(-1);
            }

            int preferredColumnHeaderHeight = 0;
            Component columnHeader = scrollPane.getColumnHeader();
            if (columnHeader != null) {
                preferredColumnHeaderHeight = columnHeader.getPreferredHeight(-1);
            }

            ScrollBarPolicy horizontalPolicy = scrollPane.getHorizontalPolicy();

            if (horizontalPolicy != ScrollBarPolicy.FILL) {
                // Get the unconstrained preferred size of the view
                Dimensions preferredViewSize = view.getPreferredSize();

                // If the policy is FILL_TO_CAPACITY, and the sum of the
                // unconstrained preferred widths of the view and the row
                // header is less than the width constraint, apply the FILL
                // policy; otherwise, apply the AUTO policy

                if (horizontalPolicy == ScrollBarPolicy.FILL_TO_CAPACITY) {
                    if (width < 0) {
                        horizontalPolicy = ScrollBarPolicy.AUTO;
                    } else {
                        int preferredWidth = preferredViewSize.width +
                            preferredRowHeaderWidth;

                        if (preferredWidth < width) {
                            horizontalPolicy = ScrollBarPolicy.FILL;
                        } else {
                            horizontalPolicy = ScrollBarPolicy.AUTO;
                        }
                    }
                }

                // If the policy is ALWAYS, NEVER, or AUTO, the preferred
                // height is the sum of the unconstrained preferred heights of
                // the view and column header, plus the height of the scroll
                // bar if policy is ALWAYS or if the view's preferred width is
                // greater than the width constraint and the policy is AUTO

                if (horizontalPolicy == ScrollBarPolicy.ALWAYS
                    || horizontalPolicy == ScrollBarPolicy.NEVER
                    || horizontalPolicy == ScrollBarPolicy.AUTO) {
                    preferredHeight = preferredViewSize.height +
                        preferredColumnHeaderHeight;

                    // If the sum of the preferred widths of the view and the
                    // row header is greater than the width constraint, include
                    // the preferred height of the scroll bar in the preferred
                    // height calculation
                    if (horizontalPolicy == ScrollBarPolicy.ALWAYS
                        || (horizontalPolicy == ScrollBarPolicy.AUTO
                        && width > 0
                        && preferredViewSize.width + preferredRowHeaderWidth > width)) {
                        preferredHeight += horizontalScrollBar.getPreferredHeight(-1);
                    }
                }
            }

            if (horizontalPolicy == ScrollBarPolicy.FILL) {
                // Preferred height is the sum of the constrained preferred height
                // of the view and the unconstrained preferred height of the column
                // header

                if (width >= 0) {
                    // Subtract the unconstrained preferred width of the row header
                    // from the width constraint
                    width = Math.max(width - preferredRowHeaderWidth, 0);
                }

                preferredHeight = view.getPreferredHeight(width) +
                    preferredColumnHeaderHeight;
            }
        }

        return preferredHeight;
    }

    @Override
    public Dimensions getPreferredSize() {
        ScrollPane scrollPane = (ScrollPane)getComponent();

        int preferredWidth = 0;
        int preferredHeight = 0;

        Component view = scrollPane.getView();
        if (view != null) {
            Dimensions preferredViewSize = view.getPreferredSize();

            preferredWidth += preferredViewSize.width;
            preferredHeight += preferredViewSize.height;

            Component rowHeader = scrollPane.getRowHeader();
            if (rowHeader != null) {
                preferredWidth += rowHeader.getPreferredWidth(-1);
            }

            Component columnHeader = scrollPane.getColumnHeader();
            if (columnHeader != null) {
                preferredHeight += columnHeader.getPreferredHeight(-1);
            }

            if (scrollPane.getHorizontalPolicy() == ScrollBarPolicy.ALWAYS) {
                preferredHeight += horizontalScrollBar.getPreferredHeight(-1);
            }

            if (scrollPane.getVerticalPolicy() == ScrollBarPolicy.ALWAYS) {
                preferredWidth += verticalScrollBar.getPreferredWidth(-1);
            }
        }

        return new Dimensions(preferredWidth, preferredHeight);
    }

    @Override
    public boolean mouseWheel(Mouse.ScrollType scrollType, int scrollAmount,
        int wheelRotation, int x, int y) {
        boolean consumed = false;

        ScrollPane scrollPane = (ScrollPane)getComponent();
        Component view = scrollPane.getView();

        if (view != null) {
            // The scroll orientation is tied to whether the shift key was
            // presssed while the mouse wheel was scrolled
            int keyboardModifiers = Keyboard.getModifiers();
            int shiftMask = Keyboard.Modifier.SHIFT.getMask();

            if ((keyboardModifiers & shiftMask) != 0) {
                // Treat the mouse wheel as a horizontal scroll event
                int previousScrollLeft = scrollPane.getScrollLeft();
                int newScrollLeft = previousScrollLeft + (scrollAmount * wheelRotation *
                    horizontalScrollBar.getUnitIncrement());

                if (wheelRotation > 0) {
                    int maxScrollLeft = getMaxScrollLeft();
                    newScrollLeft = Math.min(newScrollLeft, maxScrollLeft);

                    if (previousScrollLeft < maxScrollLeft) {
                        consumed = true;
                    }
                } else {
                    newScrollLeft = Math.max(newScrollLeft, 0);

                    if (previousScrollLeft > 0) {
                        consumed = true;
                    }
                }

                scrollPane.setScrollLeft(newScrollLeft);
            } else {
                // Treat the mouse wheel as a vertical scroll event
                int previousScrollTop = scrollPane.getScrollTop();
                int newScrollTop = previousScrollTop + (scrollAmount * wheelRotation *
                    verticalScrollBar.getUnitIncrement());

                if (wheelRotation > 0) {
                    int maxScrollTop = getMaxScrollTop();
                    newScrollTop = Math.min(newScrollTop, maxScrollTop);

                    if (previousScrollTop < maxScrollTop) {
                        consumed = true;
                    }
                } else {
                    newScrollTop = Math.max(newScrollTop, 0);

                    if (previousScrollTop > 0) {
                        consumed = true;
                    }
                }

                scrollPane.setScrollTop(newScrollTop);
            }
        }

        return consumed;
    }

    @Override
    public boolean keyPressed(int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = super.keyPressed(keyCode, keyLocation);

        if (!consumed) {
            ScrollPane scrollPane = (ScrollPane)getComponent();

            int scrollTop = scrollPane.getScrollTop();
            int scrollLeft = scrollPane.getScrollLeft();

            if (keyCode == Keyboard.KeyCode.UP) {
                int newScrollTop = Math.max(scrollTop -
                    verticalScrollBar.getUnitIncrement(), 0);

                scrollPane.setScrollTop(newScrollTop);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.DOWN) {
                int newScrollTop = Math.min(scrollTop +
                    verticalScrollBar.getUnitIncrement(), getMaxScrollTop());

                scrollPane.setScrollTop(newScrollTop);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.LEFT) {
                int newScrollLeft = Math.max(scrollLeft -
                    horizontalScrollBar.getUnitIncrement(), 0);

                scrollPane.setScrollLeft(newScrollLeft);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.RIGHT) {
                int newScrollLeft = Math.min(scrollLeft +
                    horizontalScrollBar.getUnitIncrement(), getMaxScrollLeft());

                scrollPane.setScrollLeft(newScrollLeft);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.PAGE_UP) {
                int increment = verticalScrollBar.getBlockIncrement();
                int newScrollTop = Math.max(scrollTop - increment, 0);

                scrollPane.setScrollTop(newScrollTop);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.PAGE_DOWN) {
                int increment = verticalScrollBar.getBlockIncrement();
                int newScrollTop = Math.min(scrollTop + increment, getMaxScrollTop());

                scrollPane.setScrollTop(newScrollTop);

                consumed = true;
            }
        }

        return consumed;
    }

    /**
     * Gets the maximum legal <tt>scrollTop</tt> value this this skin imposes.
     * This is the largest value possible that still shows as much of the view
     * component as it can.
     *
     * @return
     * The maximum scrollTop value
     */
    private int getMaxScrollTop() {
        int maxScrollTop = 0;

        ScrollPane scrollPane = (ScrollPane)getComponent();
        Component view = scrollPane.getView();

        if (view != null) {
            int viewHeight = view.getHeight();
            int columnHeaderHeight = 0;
            int horizontalScrollBarHeight = 0;
            int height = getHeight();

            Component columnHeader = scrollPane.getColumnHeader();
            if (columnHeader != null) {
                columnHeaderHeight = columnHeader.getHeight();
            }

            if (horizontalScrollBar.isVisible()) {
                horizontalScrollBarHeight = horizontalScrollBar.getHeight();
            }

            maxScrollTop = Math.max(viewHeight + columnHeaderHeight +
                horizontalScrollBarHeight - height, 0);
        }

        return maxScrollTop;
    }

    /**
     * Gets the maximum legal <tt>scrollLeft</tt> value this this skin imposes.
     * This is the largest value possible that still shows as much of the view
     * component as it can.
     *
     * @return
     * The maximum scrollLeft value
     */
    private int getMaxScrollLeft() {
        int maxScrollLeft = 0;

        ScrollPane scrollPane = (ScrollPane)getComponent();
        Component view = scrollPane.getView();

        if (view != null) {
            int viewWidth = view.getWidth();
            int rowHeaderWidth = 0;
            int verticalScrollBarWidth = 0;
            int width = getWidth();

            Component rowHeader = scrollPane.getRowHeader();
            if (rowHeader != null) {
                rowHeaderWidth = rowHeader.getWidth();
            }

            if (verticalScrollBar.isVisible()) {
                verticalScrollBarWidth = verticalScrollBar.getWidth();
            }

            maxScrollLeft = Math.max(viewWidth + rowHeaderWidth +
                verticalScrollBarWidth - width, 0);
        }

        return maxScrollLeft;
    }

    public void layout() {
        ScrollPane scrollPane = (ScrollPane)getComponent();

        ScrollBarPolicy horizontalPolicy = scrollPane.getHorizontalPolicy();
        ScrollBarPolicy verticalPolicy = scrollPane.getVerticalPolicy();

        boolean fillWidthToCapacity = false;
        boolean fillHeightToCapacity = false;

        // The FILL_TO_CAPACITY policy means that we try to use AUTO, and only
        // if it ends up not being wide or tall enough do we use FILL

        if (horizontalPolicy == ScrollBarPolicy.FILL_TO_CAPACITY) {
            horizontalPolicy = ScrollBarPolicy.AUTO;
            fillWidthToCapacity = true;
        }

        if (verticalPolicy == ScrollBarPolicy.FILL_TO_CAPACITY) {
            verticalPolicy = ScrollBarPolicy.AUTO;
            fillHeightToCapacity = true;
        }

        layoutHelper(horizontalPolicy, verticalPolicy);

        Component view = scrollPane.getView();
        if (view != null && (fillWidthToCapacity || fillHeightToCapacity)) {
            // We assumed AUTO. Now we check our assumption to see if we
            // need to adjust it to use FILL
            boolean adjustWidth = false, adjustHeight = false;

            if (fillWidthToCapacity) {
                Component rowHeader = scrollPane.getRowHeader();
                int rowHeaderWidth = rowHeader != null ? rowHeader.getWidth() : 0;

                int verticalScrollBarWidth = verticalScrollBar.isVisible() ?
                    verticalScrollBar.getWidth() : 0;
                int minViewWidth = getWidth() - rowHeaderWidth - verticalScrollBarWidth;

                if (view.getWidth() < minViewWidth) {
                    horizontalPolicy = ScrollBarPolicy.FILL;
                    adjustWidth = true;
                }
            }

            if (fillHeightToCapacity) {
                Component columnHeader = scrollPane.getColumnHeader();
                int columnHeaderHeight = columnHeader != null ?
                    columnHeader.getHeight() : 0;

                int horizontalScrollBarHeight = horizontalScrollBar.isVisible() ?
                    horizontalScrollBar.getHeight() : 0;
                int minViewHeight = getHeight() - columnHeaderHeight -
                    horizontalScrollBarHeight;

                if (view.getHeight() < minViewHeight) {
                    verticalPolicy = ScrollBarPolicy.FILL;
                    adjustHeight = true;
                }
            }

            if (adjustWidth || adjustHeight) {
                layoutHelper(horizontalPolicy, verticalPolicy);
            }
        }
    }

    /**
     * Layout helper method that assumes that the <tt>FILL_TO_CAPACITY</tt>
     * scroll policy doesn't exist.
     *
     * @param horizontalPolicy
     * The assumed horizontal scroll policy; musn't be <tt>FILL_TO_CAPACITY</tt>
     *
     * @param vertical policy
     * The assumed vertical scroll policy; musn't be <tt>FILL_TO_CAPACITY</tt>
     */
    private void layoutHelper(ScrollBarPolicy horizontalPolicy,
        ScrollBarPolicy verticalPolicy) {
        ScrollPane scrollPane = (ScrollPane)getComponent();

        int width = getWidth();
        int height = getHeight();

        boolean constrainWidth = (horizontalPolicy == ScrollBarPolicy.FILL);
        boolean constrainHeight = (verticalPolicy == ScrollBarPolicy.FILL);

        Component view = scrollPane.getView();
        Component columnHeader = scrollPane.getColumnHeader();
        Component rowHeader = scrollPane.getRowHeader();

        int rowHeaderWidth = 0;
        if (rowHeader != null) {
            rowHeaderWidth = rowHeader.getPreferredWidth(-1);
        }

        int columnHeaderHeight = 0;
        if (columnHeader != null) {
            columnHeaderHeight = columnHeader.getPreferredHeight(-1);
        }

        int previousViewWidth, viewWidth = 0;
        int previousViewHeight, viewHeight = 0;
        int previousHorizontalScrollBarHeight, horizontalScrollBarHeight = 0;
        int previousVerticalScrollBarWidth, verticalScrollBarWidth = 0;
        int i = 0;

        do {
            previousViewWidth = viewWidth;
            previousViewHeight = viewHeight;
            previousHorizontalScrollBarHeight = horizontalScrollBarHeight;
            previousVerticalScrollBarWidth = verticalScrollBarWidth;

            if (view != null) {
                if (constrainWidth && constrainHeight) {
                    viewWidth = Math.max
                        (width - rowHeaderWidth - verticalScrollBarWidth, 0);
                    viewHeight = Math.max
                        (height - columnHeaderHeight - horizontalScrollBarHeight, 0);
                } else if (constrainWidth) {
                    viewWidth = Math.max
                        (width - rowHeaderWidth - verticalScrollBarWidth, 0);
                    viewHeight = view.getPreferredHeight(viewWidth);
                } else if (constrainHeight) {
                    viewHeight = Math.max
                        (height - columnHeaderHeight - horizontalScrollBarHeight, 0);
                    viewWidth = view.getPreferredWidth(viewHeight);
                } else {
                    Dimensions viewPreferredSize = view.getPreferredSize();
                    viewWidth = viewPreferredSize.width;
                    viewHeight = viewPreferredSize.height;
                }
            }

            if (horizontalPolicy == ScrollBarPolicy.ALWAYS
                || (horizontalPolicy == ScrollBarPolicy.AUTO
                && viewWidth > width - rowHeaderWidth - verticalScrollBarWidth)) {
                horizontalScrollBarHeight = horizontalScrollBar.getPreferredHeight(-1);
            } else {
                horizontalScrollBarHeight = 0;
            }

            if (verticalPolicy == ScrollBarPolicy.ALWAYS
                || (verticalPolicy == ScrollBarPolicy.AUTO
                && viewHeight > height - columnHeaderHeight - horizontalScrollBarHeight)) {
                verticalScrollBarWidth = verticalScrollBar.getPreferredWidth(-1);
            } else {
                verticalScrollBarWidth = 0;
            }

            if (++i > 4) {
                // Infinite loop protection
                System.out.println("Breaking out of potential infinite loop");
                break;
            }
        } while (viewWidth != previousViewWidth
            || viewHeight != previousViewHeight
            || horizontalScrollBarHeight != previousHorizontalScrollBarHeight
            || verticalScrollBarWidth != previousVerticalScrollBarWidth);

        int scrollTop = scrollPane.getScrollTop();
        int scrollLeft = scrollPane.getScrollLeft();

        if (view != null) {
            view.setSize(viewWidth, viewHeight);
            view.setLocation(rowHeaderWidth - scrollLeft, columnHeaderHeight - scrollTop);
        }

        if (columnHeader != null) {
            columnHeader.setSize(viewWidth, columnHeaderHeight);
            columnHeader.setLocation(rowHeaderWidth - scrollLeft, 0);
        }

        if (rowHeader != null) {
            rowHeader.setSize(rowHeaderWidth, viewHeight);
            rowHeader.setLocation(0, columnHeaderHeight - scrollTop);
        }

        if (horizontalScrollBarHeight > 0) {
            horizontalScrollBar.setVisible(true);

            int horizontalScrollBarWidth = Math.max
               (width - rowHeaderWidth - verticalScrollBarWidth, 0);
            horizontalScrollBar.setSize(horizontalScrollBarWidth,
                horizontalScrollBarHeight);
            horizontalScrollBar.setLocation(rowHeaderWidth,
                height - horizontalScrollBarHeight);
        } else {
            horizontalScrollBar.setVisible(false);
        }

        if (verticalScrollBarWidth > 0) {
            verticalScrollBar.setVisible(true);

            int verticalScrollBarHeight = Math.max
               (height - columnHeaderHeight - horizontalScrollBarHeight, 0);
            verticalScrollBar.setSize(verticalScrollBarWidth,
                verticalScrollBarHeight);
            verticalScrollBar.setLocation(width - verticalScrollBarWidth,
                columnHeaderHeight);
        } else {
            verticalScrollBar.setVisible(false);
        }

        // Perform bounds checking on the scrollTop and scrollLeft values,
        // and adjust them as necessary. Make sure to do this after we've laid
        // everything out, since our ViewPortListener methods rely on valid
        // sizes from our components.

        int maxScrollTop = getMaxScrollTop();
        if (scrollTop > maxScrollTop) {
            scrollPane.setScrollTop(maxScrollTop);
        }

        int maxScrollLeft = getMaxScrollLeft();
        if (scrollLeft > maxScrollLeft) {
            scrollPane.setScrollLeft(maxScrollLeft);
        }

        // Adjust the structure of our scroll bars. Make sure to do this after
        // we adjust the scrollTop and scrollLeft values; otherwise we might
        // try to set structure values that are out of bounds.

        if (horizontalScrollBarHeight > 0) {
            int viewportWidth = Math.max(width - rowHeaderWidth -
                verticalScrollBarWidth, 0);
            horizontalScrollBar.setScope(0, viewWidth,
                Math.min(viewWidth, viewportWidth));
            horizontalScrollBar.setBlockIncrement(Math.max(1,
                viewportWidth - horizontalReveal));
        }

        if (verticalScrollBarWidth > 0) {
            int viewportHeight = Math.max(height - columnHeaderHeight -
                horizontalScrollBarHeight, 0);
            verticalScrollBar.setScope(0, viewHeight,
                Math.min(viewHeight, viewportHeight));
            verticalScrollBar.setBlockIncrement(Math.max(1,
                viewportHeight - verticalReveal));
        }
    }

    @Override
    public Object get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        Object value = null;

        if (key.equals(HORIZONTAL_INCREMENT_KEY)) {
            value = horizontalScrollBar.getUnitIncrement();
        } else if (key.equals(VERTICAL_INCREMENT_KEY)) {
            value = verticalScrollBar.getUnitIncrement();
        } else if (key.equals(HORIZONTAL_REVEAL_KEY)) {
            value = horizontalReveal;
        } else if (key.equals(VERTICAL_REVEAL_KEY)) {
            value = verticalReveal;
        } else if (key.equals(SCROLL_BAR_OPACITY_KEY)) {
            value = horizontalScrollBar.getStyles().get("backgroundOpacity");
        } else {
            value = super.get(key);
        }

        return value;
    }

    @Override
    public Object put(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        Object previousValue = null;

        if (key.equals(HORIZONTAL_INCREMENT_KEY)) {
            if (value instanceof Number) {
                value = ((Number)value).intValue();
            }

            validatePropertyType(key, value, Integer.class, false);

            previousValue = horizontalScrollBar.getUnitIncrement();
            horizontalScrollBar.setUnitIncrement((Integer)value);
        } else if (key.equals(VERTICAL_INCREMENT_KEY)) {
            if (value instanceof Number) {
                value = ((Number)value).intValue();
            }

            validatePropertyType(key, value, Integer.class, false);

            previousValue = verticalScrollBar.getUnitIncrement();
            verticalScrollBar.setUnitIncrement((Integer)value);
        } else if (key.equals(HORIZONTAL_REVEAL_KEY)) {
            if (value instanceof Number) {
                value = ((Number)value).intValue();
            }

            validatePropertyType(key, value, Integer.class, false);

            previousValue = horizontalReveal;
            horizontalReveal = (Integer)value;
        } else if (key.equals(VERTICAL_REVEAL_KEY)) {
            if (value instanceof Number) {
                value = ((Number)value).intValue();
            }

            validatePropertyType(key, value, Integer.class, false);

            previousValue = verticalReveal;
            verticalReveal = (Integer)value;
        } else if (key.equals(SCROLL_BAR_OPACITY_KEY)) {
            if (value instanceof Double) {
                value = ((Double)value).floatValue();
            }

            validatePropertyType(key, value, Float.class, false);

            previousValue = horizontalScrollBar.getStyles().get("backgroundOpacity");
            horizontalScrollBar.getStyles().put("backgroundOpacity", (Float)value);
            verticalScrollBar.getStyles().put("backgroundOpacity", (Float)value);
        } else {
            previousValue = super.put(key, value);
        }

        return previousValue;
    }

    @Override
    public Object remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        Object previousValue = null;

        if (key.equals(HORIZONTAL_INCREMENT_KEY)) {
            previousValue = put(key, DEFAULT_HORIZONTAL_INCREMENT);
        } else if (key.equals(VERTICAL_INCREMENT_KEY)) {
            previousValue = put(key, DEFAULT_VERTICAL_INCREMENT);
        } else if (key.equals(HORIZONTAL_REVEAL_KEY)) {
            previousValue = put(key, DEFAULT_HORIZONTAL_REVEAL);
        } else if (key.equals(VERTICAL_REVEAL_KEY)) {
            previousValue = put(key, DEFAULT_VERTICAL_REVEAL);
        } else if (key.equals(SCROLL_BAR_OPACITY_KEY)) {
            previousValue = put(key, DEFAULT_SCROLL_BAR_OPACITY);
        } else {
            previousValue = super.remove(key);
        }

        return previousValue;
    }

    @Override
    public boolean containsKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        return (key.equals(HORIZONTAL_INCREMENT_KEY)
            || key.equals(VERTICAL_INCREMENT_KEY)
            || key.equals(HORIZONTAL_REVEAL_KEY)
            || key.equals(VERTICAL_REVEAL_KEY)
            || key.equals(SCROLL_BAR_OPACITY_KEY));
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    // Viewport.Skin methods

    public Rectangle getViewportBounds() {
        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();

        ScrollPane scrollPane = (ScrollPane)getComponent();

        Component rowHeader = scrollPane.getRowHeader();
        if (rowHeader != null) {
            int rowHeaderWidth = rowHeader.getWidth();

            x += rowHeaderWidth;
            width -= rowHeaderWidth;
        }

        Component columnHeader = scrollPane.getColumnHeader();
        if (columnHeader != null) {
            int columnHeaderHeight = columnHeader.getHeight();

            y += columnHeaderHeight;
            height -= columnHeaderHeight;
        }

        if (horizontalScrollBar.isVisible()) {
            height -= horizontalScrollBar.getHeight();
        }

        if (verticalScrollBar.isVisible()) {
            width -= verticalScrollBar.getWidth();
        }

        return new Rectangle(x, y, width, height);
    }

    // ScrollPaneListener methods

    public void horizontalScrollBarPolicyChanged(ScrollPane scrollPane,
        ScrollBarPolicy previousHorizontalScrollBarPolicy) {
        invalidateComponent();
    }

    public void verticalScrollBarPolicyChanged(ScrollPane scrollPane,
        ScrollBarPolicy previousVerticalScrollBarPolicy) {
        invalidateComponent();
    }

    public void rowHeaderChanged(ScrollPane scrollPane, Component previousRowHeader) {
        invalidateComponent();
    }

    public void columnHeaderChanged(ScrollPane scrollPane, Component previousColumnHeader) {
        invalidateComponent();
    }

    public void cornerChanged(ScrollPane scrollPane, Component previousCorner) {
        invalidateComponent();
    }

    // ViewportListener methods

    public void scrollTopChanged(Viewport viewport, int previousScrollTop) {
        // NOTE we don't invalidate the component here because we need only
        // reposition the view and row header. Invalidating would yield
        // the correct positioning, but it would do much more work than needed.

        ScrollPane scrollPane = (ScrollPane)viewport;

        Component view = scrollPane.getView();
        Component columnHeader = scrollPane.getColumnHeader();
        Component rowHeader = scrollPane.getRowHeader();

        int scrollTop = scrollPane.getScrollTop();
        int columnHeaderHeight = 0;

        if (columnHeader != null) {
            columnHeaderHeight = columnHeader.getHeight();
        }

        if (view != null) {
            view.setLocation(view.getX(), columnHeaderHeight - scrollTop);
        }

        if (rowHeader != null) {
            rowHeader.setLocation(0, columnHeaderHeight - scrollTop);
        }

        verticalScrollBar.setValue(scrollTop);
    }

    public void scrollLeftChanged(Viewport viewport, int previousScrollLeft) {
        // NOTE we don't invalidate the component here because we need only
        // reposition the view and column header. Invalidating would yield
        // the correct positioning, but it would do much more work than needed.

        ScrollPane scrollPane = (ScrollPane)viewport;

        Component view = scrollPane.getView();
        Component columnHeader = scrollPane.getColumnHeader();
        Component rowHeader = scrollPane.getRowHeader();

        int scrollLeft = scrollPane.getScrollLeft();
        int rowHeaderWidth = 0;

        if (rowHeader != null) {
            rowHeaderWidth = rowHeader.getWidth();
        }

        if (view != null) {
            view.setLocation(rowHeaderWidth - scrollLeft, view.getY());
        }

        if (columnHeader != null) {
            columnHeader.setLocation(rowHeaderWidth - scrollLeft, 0);
        }

        horizontalScrollBar.setValue(scrollLeft);
    }

    public void viewChanged(Viewport viewport, Component previousView) {
        invalidateComponent();
    }

    // ScrollBarValueListener methods

    public void valueChanged(ScrollBar scrollBar, int previousValue) {
        ScrollPane scrollPane = (ScrollPane)getComponent();

        int value = scrollBar.getValue();

        if (scrollBar == horizontalScrollBar) {
            scrollPane.setScrollLeft(value);
        } else {
            scrollPane.setScrollTop(value);
        }
    }
}
