package com.minispring.lifecycle;

import com.minispring.core.Container;

/**
 * Optional lifecycle callback executed after construction and before the
 * final bean post-processing stage.
 */
public interface Initializable {

    void initialize();
}
