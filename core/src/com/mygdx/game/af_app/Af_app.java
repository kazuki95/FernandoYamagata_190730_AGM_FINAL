package com.mygdx.game.af_app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class Af_app extends ApplicationAdapter {

	private SpriteBatch batch;


	//atribuindo variaveis para as texturas
	private Texture passaros;
	private Texture fundo;
	private Texture canoAlto;
	private Texture canoBaixo;
	private Texture GameOver;
	private Texture moedaPrata;
	private Texture moedaOuro;
	private Texture logo;

	//Textos que aparecerão na tela
	BitmapFont textPontuacao;
	BitmapFont textRenicia;
	BitmapFont textMelhorPontuacao;


	//booleano para checar se passou pelos canos
	private boolean passouCano = false;


	//variavel de Random para deixar aleatorio
	private Random random;


	// variaveis
	private int pontuacaoMaxima = 0;
	private int pontos = 0;
	private int gravidade = 0;
	private int estadojogo = 0;
	private int moedaValor = 0;
	int valor = 1;

	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float espacoEntreCanos;
	private float posicaoHorizontalPassaro = 0;
	private float posicaoOuro;
	private float posicaoPrata;
	private float posicaoMoedaVertical;

	private ShapeRenderer shapeRenderer;

	// variaveis para a colisão dos objetos
	private Circle circuloPassaro;
	private Rectangle retaguloCanoCima;
	private Rectangle retanguloCanoBaixo;
	private Circle ciculoMoedaOuro;
	private Circle ciculoMoedaPrata;

	//Soms do jogo
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoedas;

	Preferences preferencias;



	@Override
	public void create() {
		//metodos criados para inicializar objetos e texturas
		inicializaTexuras();
		inicializarObjetos();

	}

	private void inicializarObjetos() {
		//inicializando objetos
		batch = new SpriteBatch();
		random = new Random();

		// puxando da biblioteca para adaptar o fundo com o celular
		alturaDispositivo = Gdx.graphics.getHeight();
		larguraDispositivo = Gdx.graphics.getWidth();

		//fazer passaro e moeda nascer no meio da tela
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoMoedaVertical = alturaDispositivo / 2;
		posicaoOuro = larguraDispositivo;
		posicaoPrata = larguraDispositivo;


		//inicializando canos do jogo
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		//pontuação do jogo
		textPontuacao = new BitmapFont();
		textPontuacao.setColor(Color.WHITE);
		textPontuacao.getData().setScale(10);

		//Reiniciar o jogo
		textRenicia = new BitmapFont();
		textRenicia.setColor(Color.RED);
		textRenicia.getData().setScale(3);

		//Melhor pontuação do jogo
		textMelhorPontuacao = new BitmapFont();
		textMelhorPontuacao.setColor(Color.GOLDENROD);
		textMelhorPontuacao.getData().setScale(3);

		//Inicializando os objetos de colisão(collider)
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retaguloCanoCima = new Rectangle();
		retanguloCanoBaixo = new Rectangle();
		ciculoMoedaOuro = new Circle();
		ciculoMoedaPrata = new Circle();

		//soms do jogo
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoedas = Gdx.audio.newSound(Gdx.files.internal("ponto.mp3"));

		//salvar o valor da pontuação maxima
		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

	}

	private void inicializaTexuras() {
		//inicializando texturas

		//fazendo aparecer o fundo
		fundo = new Texture("fundo.png");

		//fazendo aparecer o passaro
		passaros = new Texture("angry.png");

		//fazendo aparecer os canos
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");

		//fazendo aparecer as moedas
		moedaOuro = new Texture("ouro.png");
		moedaPrata = new Texture("prata.png");

		//fazendo aparecer o GameOver
		GameOver = new Texture("game_over.png");

		//fazendo aparecer a logo do jogo
		logo = new Texture("inicio.jpg");

	}

	@Override
	public void render() {

		/*metodos criados para verificar estados do jogo, renderizar as texuras no jogo,
		 detectar colisões no jogo e validar os pontos.*/
		verificaEstadojogo();
		desenharTexturas();
		detectarColisao();
		validarPontos();

	}

	private void detectarColisao() {

		//collider do passaro
		circuloPassaro.set(50 + passaros.getWidth() / 2f,
				posicaoInicialVerticalPassaro + passaros.getHeight() / 2f,
				passaros.getWidth() / 2f);

		//collider do cano de baixo
		retanguloCanoBaixo.set(posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		//collider do cano de cima
		retaguloCanoCima.set(posicaoCanoHorizontal,
				alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoAlto.getWidth(), canoAlto.getHeight());


		//collider das moedas
		ciculoMoedaPrata.set(posicaoPrata, alturaDispositivo /2 + posicaoMoedaVertical + moedaPrata.getHeight() / 2f,
				moedaPrata.getWidth() / 2f);
		ciculoMoedaOuro.set(posicaoOuro, alturaDispositivo /2 + posicaoMoedaVertical + moedaOuro.getHeight() / 2f,
				moedaOuro.getWidth() / 2f);

		//identificar se o passaro bateu nas moedas
		boolean bateuMoedaOuro = Intersector.overlaps(circuloPassaro, ciculoMoedaOuro);
		boolean bateuMoedaPrata = Intersector.overlaps(circuloPassaro, ciculoMoedaPrata);

		//identificar se o passaro bateu nos canos
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retaguloCanoCima);
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

		// se bateu no cano
		if (bateuCanoBaixo || bateuCanoCima) {
			//se o estado do jogo for 1
			if (estadojogo == 1)
			{
				//reproduz o som de colisao
				somColisao.play();
				//muda o estado do jogo de 1 para 2
				estadojogo = 2;
			}
		}

		// se bateu na moeda de ouro
		if (bateuMoedaOuro)
		{
			//se o estado do jogo for 1
			if (estadojogo == 1)
			{
				//adiciona 10 pontos na pontuação
				pontos += 10;
				moedaValor = 0;
				//reproduz o som de colisao na moeda
				somMoedas.play();
				//posição da moeda
				posicaoOuro = larguraDispositivo;

			}
		}

		// se bateu na moeda de prata
		if (bateuMoedaPrata)
		{
			//se o estado do jogo for 1
			if (estadojogo == 1)
			{
				//adiciona 5 pontos na pontuação
				pontos += 5;
				moedaValor++;
				//reproduz o som de colisao na moeda
				somMoedas.play();
				//posição da moeda
				posicaoPrata = larguraDispositivo;


			}
		}
	}

	private void validarPontos() {

		//verificar se passou o cano
		if (posicaoCanoHorizontal < 50 - passaros.getWidth())
		{
			//se passou o cano
			if (!passouCano)
			{
				//adiciona ponto
				pontos++;
				//muda passouCano para verdadeiro
				passouCano = true;
				//reproduz o som que passou pelo cano
				somPontuacao.play();

			}
		}
	}

	private void verificaEstadojogo() {

		//Verificar toque na tela
		boolean toqueTela = Gdx.input.justTouched();

		//inicia quando tocar na tela
		if (estadojogo == 0)
		{
			// fazer o passaro subir ao tocar na tela para iniciar o jogo
			if (toqueTela)
			{
				gravidade = -15;
				//altera o estado do jogo de 0 para 1
				estadojogo = 1;
				//reproduz o som que está "voando"
				somVoando.play();
			}

		}
		//verifica que o jogador está jogando
		else if (estadojogo == 1) {
			valor = 0;
			// fazer o passaro subir ao tocar na tela
			if (toqueTela)
			{
				gravidade = -15;
				//reproduz o som que está "voando"
				somVoando.play();
			}

			//velocidade dos canos
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;

			//aparição dos canos
			if (posicaoCanoHorizontal < -canoBaixo.getWidth())
			{
				posicaoCanoHorizontal = larguraDispositivo;
				//randomizar a aparição dos canos
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}

			posicaoPrata -= Gdx.graphics.getDeltaTime() * 150;

			//aparição das moedas
			if (posicaoPrata < -moedaPrata.getWidth())
			{
				posicaoPrata = larguraDispositivo;
				posicaoMoedaVertical = random.nextInt(300) - 200;

			}
			if (moedaValor >= 3)
			{
				posicaoOuro -= Gdx.graphics.getDeltaTime() * 150;
				if (posicaoOuro < -moedaOuro.getWidth())
				{
					posicaoOuro = larguraDispositivo;
					posicaoMoedaVertical = random.nextInt(300) - 200;
					moedaValor = 0;
				}
			}
			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

			//adiciona gravidade
			gravidade++;

		}

		//jogador morreu no jogo
		else if (estadojogo == 2)
		{
			//salvar a nova pontuação maxima caso seja maior que a anterior
			if (pontos > pontuacaoMaxima)
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
			}

			//animação de quando o jogador bate no cano
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;


			//reiniciando os atributos para reiniciar o jogo quando o jogador perde
			if (toqueTela)
			{
				estadojogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo / 2;
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoOuro = larguraDispositivo;
				posicaoPrata = larguraDispositivo;
				moedaValor = 0;
			}
		}


	}

	private void desenharTexturas()
	{
		// inicia renderização
		batch.begin();

		// puxa a imagem que colocou no create do fundo
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);


		if (estadojogo == 0 && valor == 1 )
		{
			//inicia a logo
			batch.draw(logo,posicaoHorizontalPassaro, alturaDispositivo /4,1200,800);
		}

		// puxa a imagem que colocou no create do passaro
		batch.draw(passaros, 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);

		// puxa a imagem que colocou no create dos canos
		batch.draw(canoBaixo, posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
		batch.draw(canoAlto, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);


		// puxa as imagens das moedas
		if (moedaValor <= 3)
		{
			batch.draw(moedaPrata, posicaoPrata, alturaDispositivo /2 + posicaoMoedaVertical + moedaPrata.getHeight() / 2f);
		}

		if (moedaValor >= 3)
		{
			batch.draw(moedaOuro, posicaoOuro, alturaDispositivo /2 + posicaoMoedaVertical + moedaOuro.getHeight() / 2f);
		}


		if(estadojogo == 1)
		{
			//texto da pontuação no jogo
			textPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 100);

		}



		//se o estado do jogo for 2
		if (estadojogo == 2)
		{
			// puxa a imagem que colocou no create do Game Over
			batch.draw(GameOver, larguraDispositivo / 2 - GameOver.getWidth() / 2f, alturaDispositivo / 2);
			// puxa a imagem que colocou no create do reiniciar
			textRenicia.draw(batch, "Toque  na tela para reiniciar!", larguraDispositivo / 2 - 250, alturaDispositivo / 2 - GameOver.getHeight() / 2 - 300);
			// puxa a imagem que colocou no create da melhor pontuação
			textMelhorPontuacao.draw(batch, "Sua melhor pontuação  é : " + pontuacaoMaxima + " Pontos", larguraDispositivo / 2 - 300, alturaDispositivo / 2 - GameOver.getHeight() * 2);
		}


		//termina a sequencia da aplicação.
		batch.end();

	}

	@Override
	public void dispose() {

	}

}
