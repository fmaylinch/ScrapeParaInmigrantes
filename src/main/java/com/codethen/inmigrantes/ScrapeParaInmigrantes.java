package com.codethen.inmigrantes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exam scraping.
 *
 * Exams from here: http://www.parainmigrantes.info/test-de-nacionalidad-espanola/
 */
public class ScrapeParaInmigrantes {

	public static void main(String[] args) throws IOException {

		List<String> urls = Arrays.asList(

			"http://www.parainmigrantes.info/examen-nacionalidad-espanola-examen-oficial-ccse-mayo-2016-2a-convocatoria/"
		);

		for (String url : urls) {
			scrapeUrl(url);
		}
	}

	/**
	 * Prints questions and answers of the exam in the url
	 * (correct answers are printed at the end)
	 */
	private static void scrapeUrl(String url) throws IOException
	{
		System.out.println(url);
		System.out.println();

		// Note: needs user agent or throws 403 -- http://stackoverflow.com/a/10136686/1121497
		Document doc = Jsoup.connect(url).userAgent("Mozilla").get();

		Elements questions = doc.select(".mtq_question");

		List<String> answerLetters = new ArrayList<>();

		for (Element question : questions) {

			String questionText = question.select(".mtq_question_text").text();
			System.out.println(question.siblingIndex() + " - " + questionText);
			Elements answers = question.select(".mtq_answer_table .mtq_clickable");

			String correctLetter = null;

			for (Element answer : answers) {

				String letter = answer.select(".mtq_css_letter_button").text();
				String correctOrWrong = answer.select(".mtq_marker").first().attr("alt");

				if (!correctOrWrong.equals("Correct") && !correctOrWrong.equals("Wrong")) {
					throw new RuntimeException("Unexpected marker '" + correctOrWrong + "' in question " + question.siblingIndex());
				}

				boolean correct = correctOrWrong.equals("Correct");
				if (correct) {
					if (correctLetter != null) {
						throw new RuntimeException("Unexpectedly, question " + question.siblingIndex() + " has two correct answers: " + correctLetter + " and " + letter);
					}
					correctLetter = letter;
					answerLetters.add(correctLetter);
				}

				String answerText = answer.select(".mtq_answer_text").text();
				System.out.println(letter + ". " + answerText);
			}

			System.out.println();
		}

		System.out.println("Respuestas:");

		int questionIndex = 1;
		for (String answerLetter : answerLetters) {
			System.out.print(questionIndex + ":" + answerLetter + " ");
			questionIndex++;
		}

		System.out.println();
		System.out.println("---------------");
		System.out.println();
	}
}
