package net.masterthought.cucumber;

import static net.masterthought.cucumber.FileReaderUtil.getAbsolutePathFromResource;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.Row;
import net.masterthought.cucumber.json.Step;
import net.masterthought.cucumber.json.support.Status;

public class StepTest {

    private final Configuration configuration = new Configuration(new File(""), "testProject");

    private Step passingStep;
    private Step failingStep;
    private Step skippedStep;
    private Step withOutput;

    @Before
    public void setUpJsonReports() throws IOException {
        configuration.setStatusFlags(false, false, false, false);

        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/project1.json"));
        List<Feature> features = new ReportParser(configuration).parseJsonResults(jsonReports);
        Feature passingFeature = features.get(0);
        Feature failingFeature = features.get(1);

        passingStep = passingFeature.getElements()[0].getSteps()[0];
        failingStep = failingFeature.getElements()[0].getSteps()[5];
        skippedStep = failingFeature.getElements()[0].getSteps()[6];
        withOutput = passingFeature.getElements()[1].getSteps()[0];
    }

    @Test
    public void shouldReturnRows() throws IOException {
        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/cells.json"));
        List<Feature> features = new ReportParser(configuration).parseJsonResults(jsonReports);
        Feature feature = features.get(0);
        Step step = feature.getElements()[0].getSteps()[0];

        assertThat(step.getRows()[0], isA(Row.class));
    }

    @Test
    public void shouldKnowIfHasRows() {
        assertThat(passingStep.hasRows(), is(false));
    }

    @Test
    public void shouldReturnOutput() {
        // ids are generated by Object.hashCode which is different each time so this must be replaced
        assertThat(withOutput.getOutput().replaceAll("output_[-\\d]+", "output_1234"),
                is("<a onclick=\"message=document.getElementById('output_1234'); message.className = (message.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Output:</a><div id=\"output_1234\" class=\"hidden\"><pre>some other text\nwooops\nmatchedColumns</pre></div>"));
    }

    @Test
    public void shouldReturnStatus() {
        assertThat(passingStep.getStatus(), is(Status.PASSED));
        assertThat(failingStep.getStatus(), is(Status.FAILED));
    }

    @Test
    public void shouldReturnDuration() {
        assertThat(passingStep.getDuration(), is(107447000L));
    }

    @Test
    public void shouldNotCreateLinkToScreenshotWhenOneDoesNotExist() throws IOException {
        long screenshotTime = new DateTime().getMillis();
        DateTimeUtils.setCurrentMillisFixed(screenshotTime);
        assertThat(failingStep.getAttachments(), is(EMPTY));
    }

    @Test
    public void shouldCreateLinkToScreenshotWhenOneExists() throws IOException {
        // ids are generated by Object.hashCode which is different each time so this must be replaced
        assertThat(failingStepWithEmbeddedScreenshot().getAttachments().replaceAll("embedding_[-\\d]+", "embedding_1234"),
                is("<div class=\"embedding indention\"><a onclick=\"attachment=document.getElementById('embedding_1234'); attachment.className = (attachment.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Attachment 1 (png)</a><div id=\"embedding_1234\" class=\"hidden\"><img id=\"embedding_1234\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH1gcBFzozgT/kfQAAAB10RVh0Q29tbWVudABDcmVhdGVkIHdpdGggVGhlIEdJTVDvZCVuAAABgUlEQVQ4y8WTMU+UQRCGn5ldwC8GKbAywcZCKOzMNSbGGH8B5kIiMdJRWkgDP8BrbCztoLAgGBNt7EjgriSn0dpYcHQf3x1Q3F1gZyzAky+oOWPhJps3O+/k2Z3ZXfjfQwCqc9Wnol5z86xkqnTdZHljfePl7wDxNNFrC08WsokrEyXz4PAgW11brQF/Brh5dml0jHpju2RWbldw86w699DPxzWEXcQW11+/+RB/BA+Pjuj3+yVAvr/P/KP5C7u29lpT9XrjFXB9AOh0OnS7vVJi82Pzl8eevjmNWZoalABQtNv0er2hOl+02+UeABRFMTygKC4C8jwfGpDn+c+rflxZ/Ixxy8X/8gEJCF+iiMzcm70DQIgBVUVEcHfcHEs2mOkkYSmRkgGws/VpJlqy7bdr7++PXx4nngGCalnDuXU41W+tFiM69i6qyrPESfPqtUmJMaCiiAoigorAmYoKKgoIZgmP5lFDTQDu3njwPJGWcEaGql/kGHjR+Lq58s+/8TtoKJeZGE46kQAAAABJRU5ErkJggg==\"></div></div>"
                 + "<div class=\"embedding indention\"><a onclick=\"attachment=document.getElementById('embedding_1234'); attachment.className = (attachment.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Attachment 2 (png)</a><div id=\"embedding_1234\" class=\"hidden\"><img id=\"embedding_1234\" src=\"data:image/png;base64,R0lGODlhDwAPAKECAAAAzMzM/////wAAACwAAAAADwAPAAACIISPeQHsrZ5ModrLlN48CXF8m2iQ3YmmKqVlRtW4MLwWACH+H09wdGltaXplZCBieSBVbGVhZCBTbWFydFNhdmVyIQAAOw==\"></div></div>"
                 + "<div class=\"embedding indention\"><a onclick=\"attachment=document.getElementById('embedding_1234'); attachment.className = (attachment.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Attachment 3 (jpeg)</a><div id=\"embedding_1234\" class=\"hidden\"><img id=\"embedding_1234\" src=\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSgBBwcHCggKEwoKEygaFhooKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKP/AABEIAAEAAQMBEQACEQEDEQH/xAGiAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgsQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+gEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoLEQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APlq7uZ7y6muryaWe5mdpJZZXLPI7HJZieSSSSSaAP8A/9k=\"></div></div>"
                 + "<div class=\"embedding indention\"><a onclick=\"attachment=document.getElementById('embedding_1234'); attachment.className = (attachment.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Attachment 4 (plain text)</a><div id=\"embedding_1234\" class=\"hidden\"><pre>java.lang.Throwable</pre></div></div>"
                 + "<div class=\"embedding indention\"><a onclick=\"attachment=document.getElementById('embedding_1234'); attachment.className = (attachment.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Attachment 5 (HTML)</a><div id=\"embedding_1234\" class=\"hidden\"><i>Hello</i> <b>World!<b></div></div>"
                 + "<div class=\"embedding indention\"><a onclick=\"attachment=document.getElementById('embedding_1234'); attachment.className = (attachment.className == 'hidden' ? 'visible' : 'hidden'); return false\" href=\"#\">Attachment 6 (unknown)</a><div id=\"embedding_1234\" class=\"hidden\">File the <a href=\"https://github.com/damianszczepanik/cucumber-reporting/issues\">bug</a> so support for this mimetype can be added.</div></div>"));
    }

    private Step failingStepWithEmbeddedScreenshot() throws IOException {
        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/embedded_image.json"));
        List<Feature> features = new ReportParser(configuration).parseJsonResults(jsonReports);
        Feature failingFeatureWithEmbeddedScreenshot = features.get(0);
        return failingFeatureWithEmbeddedScreenshot.getElements()[0].getSteps()[2];
    }
}
