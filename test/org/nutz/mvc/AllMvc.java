package org.nutz.mvc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.mvc.adaptor.JsonAdaptorTest;
import org.nutz.mvc.adaptor.injector.AllInjector;
import org.nutz.mvc.init.MvcBaseTest;
import org.nutz.mvc.init.MvcErrorCatchTest;
import org.nutz.mvc.init.PathNodeTest;
import org.nutz.mvc.upload.unit.UploadingUnitTest;
import org.nutz.mvc.upload.util.BufferRingTest;
import org.nutz.mvc.upload.util.RemountBytesTest;
import org.nutz.mvc.view.AllView;
import org.nutz.mvc.view.DefaultViewMakerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({	PathNodeTest.class,
						MvcBaseTest.class,
						MvcErrorCatchTest.class,
						RemountBytesTest.class,
						BufferRingTest.class,
						UploadingUnitTest.class,
						JsonAdaptorTest.class,
						AllInjector.class,
						AllView.class,
						DefaultViewMakerTest.class})
public class AllMvc {}
