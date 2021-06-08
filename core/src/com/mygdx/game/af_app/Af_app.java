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
	//para guardar as  texturas  que vão ser renderizadas
	private Texture passaros;
	private Texture fundo;
	private Texture canoAlto;
	private Texture canoBaixo;
	private Texture GameOver;
	private Texture moedaPrata;
	private Texture moedaOuro;
	private Texture logo;

	BitmapFont textPontuacao;// para mostrar texto de pontos
	BitmapFont textRenicia; // para mostrar remiciar jogo
	BitmapFont textMelhorPontuacao; //para mostra melhor pontuação

	private boolean passouCano = false; // virificar se passou pelo cano se verdadeiro ou falso

	private Random random; // var para random

	private int pontuacaoMaxima = 0;
	private int pontos = 0; // var de pontos
	private int gravidade = 0; // var de gravidade
	private int estadojogo = 0; // var de estado do jogo
	private int moedapravalor = 0;
	int valor = 1;

	private float posicaoInicialVerticalPassaro = 0;// posição do passaro  na vertical
	private float posicaoCanoHorizontal; //posição cano horizontal
	private float posicaoCanoVertical; //posição cano vertical
	private float larguradispositivo;// a largura do dispositivo
	private float alturadispositivo;// a altura do dispositivo
	private float espacoEntreCanos;// espaço entre os canos
	private float posicaoHorizontalPassaro = 0;
	private float posicaoOuro;
	private float posicaoPrata;
	private float posicaomoedavetical;

	private ShapeRenderer shapeRenderer;
	//  vars de colisão
	private Circle circuloPassaro;
	private Rectangle retaguloCanoCima;
	private Rectangle retanguloBaixo;
	private Circle ciculoMoedaOuro;
	private Circle ciculoMoedaPrata;

	// som do jogo
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoedas;

	Preferences preferencias;



	@Override
	public void create() {
		inicializaTexuras();// metodo para inicializar as texturas
		inicializarObjetos();// metodo para inicializar os objetos

	}

	private void inicializarObjetos() {

		batch = new SpriteBatch();
		random = new Random();//random

		alturadispositivo = Gdx.graphics.getHeight(); // declarando que altura e e a mesma do dispositivo
		larguradispositivo = Gdx.graphics.getWidth(); // declarando que largura e e a mesma do dispositivo
		posicaoInicialVerticalPassaro = alturadispositivo / 2; // para pegar a metade da altura do dispositivo
		posicaomoedavetical = alturadispositivo / 2;
		posicaoCanoHorizontal = larguradispositivo; //possição do cano sera a largura do dispositivo
		posicaoOuro = larguradispositivo;
		posicaoPrata = larguradispositivo;

		espacoEntreCanos = 350;

		textPontuacao = new BitmapFont(); // pegando a texto de pontos
		textPontuacao.setColor(Color.WHITE); // deixando ele com  cor branca
		textRenicia = new BitmapFont(); // pegando a texto de pontos
		textPontuacao.getData().setScale(10); // determinado tamanho do texto

		textRenicia.setColor(Color.GREEN); // deixando ele com  cor branca
		textRenicia.getData().setScale(3); // determinado tamanho do texto

		textMelhorPontuacao = new BitmapFont(); // pegando a texto de pontos
		textMelhorPontuacao.setColor(Color.RED); // deixando ele com  cor branca
		textMelhorPontuacao.getData().setScale(3); // determinado tamanho do texto

		// pegando as colisões
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retaguloCanoCima = new Rectangle();
		retanguloBaixo = new Rectangle();
		ciculoMoedaOuro = new Circle();
		ciculoMoedaPrata = new Circle();

		// pegando  som do audios
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoedas = Gdx.audio.newSound(Gdx.files.internal("ponto.mp3"));

		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);// para gardar maior pontuação

	}

	private void inicializaTexuras() {

		fundo = new Texture("fundo.png");//pegando a textura para criar

		// pondo as pngs do passaro em um arrey de texturas para que forme animação na cena
		passaros = new Texture("angry.png");

		// pegando texturas do cano
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");

		moedaOuro = new Texture("ouro.png");
		moedaPrata = new Texture("prata.png");

		GameOver = new Texture("game_over.png");
		logo = new Texture("logo.jpg");

	}

	@Override
	public void render() {

		verificaEstadojogo();// verificando estados do jogo
		desenharTexturas();// renderizando as texuras ou desenhando na cena
		detectarColisao();// detectar as colisões  dos objetos na cena
		validarPontos();// validando pontos quando passsa entre os canos

	}

	private void detectarColisao() {

		circuloPassaro.set(50 + passaros.getWidth() / 2f,
				posicaoInicialVerticalPassaro + passaros.getHeight() / 2f,
				passaros.getWidth() / 2f);// pondo colisao no passaro

		retanguloBaixo.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());// pondo colisão nos canos

		retaguloCanoCima.set(posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth(), canoAlto.getHeight());// pondo colisão nos canos

		ciculoMoedaPrata.set(posicaoPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f,
				moedaPrata.getWidth() / 2f);

		ciculoMoedaOuro.set(posicaoOuro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f,
				moedaOuro.getWidth() / 2f);

		boolean beteumoedaOuro = Intersector.overlaps(circuloPassaro, ciculoMoedaOuro);
		boolean beteumoedaPrata = Intersector.overlaps(circuloPassaro, ciculoMoedaPrata);
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retaguloCanoCima);// verificando a colisão entre cano e passaro
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloBaixo);// verificando a colisão entre cano e passaro
		if (bateuCanoBaixo || bateuCanoCima) {
			// se o estado  for 1 dispara som da colisão e muda estado do jogo para 2
			if (estadojogo == 1) {
				somColisao.play();// dispara som de colisão
				estadojogo = 2;// estado do jogo

			}
		}

		if (beteumoedaOuro) {
			if (estadojogo == 1) {
				pontos += 10;
				moedapravalor = 0;
				somMoedas.play();
				posicaoOuro = larguradispositivo;

			}
		}
		if (beteumoedaPrata) {

			if (estadojogo == 1) {
				pontos += 5;
				moedapravalor++;
				somMoedas.play();
				posicaoPrata = larguradispositivo;

			}
		}
	}

	private void validarPontos() {
		if (posicaoCanoHorizontal < 50 - passaros.getWidth()) {// se passar pelo cano
			if (!passouCano) {// for diferente passouCano
				pontos++; // soma pontos
				passouCano = true;// e passouCano é verdadeiro
				somPontuacao.play();// dispara som de pontuação

			}
		}
	}

	private void verificaEstadojogo() {

		boolean toqueTela = Gdx.input.justTouched();// bool pra verificar toque
		// estado  0 , se tocar na tela gravidade  do passaro puxa pra baixo, e muda estado do jogo pra 1 e dispara som de voando
		if (estadojogo == 0) {
			if (Gdx.input.justTouched()) {
				gravidade = -15;
				estadojogo = 1;
				somVoando.play();
			}

		}
		// estado do jogo  1 ativa gravidade e dispara som de voando, e faz que o cano comece a se movimentar,
		else if (estadojogo == 1) {
			valor = 0;
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;// velocidade do cano
			if (posicaoCanoHorizontal < -canoBaixo.getWidth()) {
				posicaoCanoHorizontal = larguradispositivo; // posição cano na horrizontal é igual a largura
				posicaoCanoVertical = random.nextInt(400) - 200;// posição vertical fica randomicamente mudando de valores
				passouCano = false;
			}

			posicaoPrata -= Gdx.graphics.getDeltaTime() * 150;
			if (posicaoPrata < -moedaPrata.getWidth()) {
				posicaoPrata = larguradispositivo;
				posicaomoedavetical = random.nextInt(300) - 200;

			}

			if (moedapravalor >= 3) {
				posicaoOuro -= Gdx.graphics.getDeltaTime() * 150;
				if (posicaoOuro < -moedaOuro.getWidth()) {
					posicaoOuro = larguradispositivo;
					posicaomoedavetical = random.nextInt(300) - 200;
					moedapravalor = 0;
				}
			}

			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

			gravidade++;// almentando a gravidade

		}

		// estado do jogo  2 , faz que  se coliu com os canos, motre a melhor pontuação e resete o game se tocar na tela novamente
		else if (estadojogo == 2) {
			if (pontos > pontuacaoMaxima) // se pontuação  for  maior  que pontuação maxima, potuanção maxima  vai ser igual a pontos
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500; // efeito de colisãp quando acontecer

			if (toqueTela) // se tocou na tela, reseta o estado do jogo como pontuação, posição do passaro e canos
			{
				estadojogo = 0;// estado do jogo
				pontos = 0;// pontos
				gravidade = 0;// garvidade
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturadispositivo / 2;
				posicaoCanoHorizontal = larguradispositivo;
				posicaoOuro = larguradispositivo;
				posicaoPrata = larguradispositivo;
				moedapravalor = 0;
			}
		}


	}

	private void desenharTexturas() {
		batch.begin();// coemeçando

		batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// rederizando o fundo do game na cena
		if (estadojogo == 0 && valor == 1 )
		{
			batch.draw(logo,posicaoHorizontalPassaro,alturadispositivo /4,1200,800);

		}

		batch.draw(passaros, 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);// rederizando o passaro na cena
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);//renderizando cano na cena e calculando conforme o tamanho da tela
		batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);//renderizando cano na cena e calculando conforme o tamanho da tela


		if (moedapravalor <= 3) {

			batch.draw(moedaPrata, posicaoPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f);
		}

		if (moedapravalor >= 3) {

			batch.draw(moedaOuro, posicaoOuro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f);

		}



		if(estadojogo == 1)
		{
			textPontuacao.draw(batch, String.valueOf(pontos), larguradispositivo / 2, alturadispositivo - 100);// renderizando os pontos na tela  cada vez que passar entre os canos

		}




		// se o estado do jogo for  2 renderiza na tela a pngs  na tela dando infomação sobre o estatus que jogo se encotra
		if (estadojogo == 2) {
			batch.draw(GameOver, larguradispositivo / 2 - GameOver.getWidth() / 2f, alturadispositivo / 2);
			textRenicia.draw(batch, "Toque  na tela para reiniciar!", larguradispositivo / 2 - 200, alturadispositivo / 2 - GameOver.getHeight() / 2f);
			textMelhorPontuacao.draw(batch, "Sua melhor pontuação  é : " + pontuacaoMaxima + " Pontos", larguradispositivo / 2 - 300, alturadispositivo / 2 - GameOver.getHeight() * 2);
		}



		batch.end();

	}

	@Override
	public void dispose() {

	}

}
