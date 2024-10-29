CREATE TABLE public.cards (
    id bigint NOT NULL,
    enable boolean,
    last_try timestamp without time zone,
    name1 character varying(255),
    name2 character varying(255),
    period_days_reminder integer,
    deck_id bigint NOT NULL
);


ALTER TABLE public.cards OWNER TO user_db;

--
-- TOC entry 214 (class 1259 OID 34692)
-- Name: cards_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.cards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.cards_id_seq OWNER TO user_db;

--
-- TOC entry 3370 (class 0 OID 0)
-- Dependencies: 214
-- Name: cards_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.cards_id_seq OWNED BY public.cards.id;


--
-- TOC entry 217 (class 1259 OID 34702)
-- Name: decks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.decks (
    id bigint NOT NULL,
    description character varying(255),
    name character varying(255),
    language_id bigint NOT NULL,
    username_id bigint
);


ALTER TABLE public.decks OWNER TO user_db;

--
-- TOC entry 216 (class 1259 OID 34701)
-- Name: decks_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.decks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.decks_id_seq OWNER TO user_db;

--
-- TOC entry 3371 (class 0 OID 0)
-- Dependencies: 216
-- Name: decks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.decks_id_seq OWNED BY public.decks.id;


--
-- TOC entry 2227 (class 1259 OID 34737)
-- Name: languages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.languages (
    id integer NOT NULL,
    language character varying,
    image character varying(255),
    userId bigint
);


ALTER TABLE public.languages OWNER TO user_db;

--
-- TOC entry 221 (class 1259 OID 34736)
-- Name: languages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.languages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.languages_id_seq OWNER TO user_db;

--
-- TOC entry 3372 (class 0 OID 0)
-- Dependencies: 221
-- Name: languages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.languages_id_seq OWNED BY public.languages.id;


--
-- TOC entry 218 (class 1259 OID 34710)
-- Name: user_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    roles character varying(255) NOT NULL,
    primary key(user_id,roles)
);


ALTER TABLE public.user_roles OWNER TO user_db;

--
-- TOC entry 220 (class 1259 OID 34714)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    avatar character varying(255),
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT null,
    level bigint not null,
    experience bigint not null,
    log_streak bigint not null,
    gems bigint not null,
    isEnabled boolean not null
);


ALTER TABLE public.users OWNER TO user_db;

--
-- TOC entry 219 (class 1259 OID 34713)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO user_db;

--
-- TOC entry 3373 (class 0 OID 0)
-- Dependencies: 219
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 3192 (class 2604 OID 34696)
-- Name: cards id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cards ALTER COLUMN id SET DEFAULT nextval('public.cards_id_seq'::regclass);


--
-- TOC entry 3193 (class 2604 OID 34705)
-- Name: decks id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks ALTER COLUMN id SET DEFAULT nextval('public.decks_id_seq'::regclass);


--
-- TOC entry 3195 (class 2604 OID 34740)
-- Name: languages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.languages ALTER COLUMN id SET DEFAULT nextval('public.languages_id_seq'::regclass);


--
-- TOC entry 3194 (class 2604 OID 34717)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (id);


--
-- TOC entry 3199 (class 2606 OID 34709)
-- Name: decks decks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT decks_pkey PRIMARY KEY (id);


--
-- TOC entry 3207 (class 2606 OID 34751)
-- Name: languages languages_language_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.languages
    ADD CONSTRAINT languages_language_key UNIQUE (language);


--
-- TOC entry 3209 (class 2606 OID 34744)
-- Name: languages languages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.languages
    ADD CONSTRAINT languages_pkey PRIMARY KEY (id);


--
-- TOC entry 3201 (class 2606 OID 34723)
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- TOC entry 3203 (class 2606 OID 34725)
-- Name: users uk_r43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- TOC entry 3205 (class 2606 OID 34721)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3211 (class 2606 OID 34745)
-- Name: decks fk_language; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES public.languages(id) NOT VALID;


--
-- TOC entry 3212 (class 2606 OID 34752)
-- Name: decks fk_username; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.decks
    ADD CONSTRAINT fk_username FOREIGN KEY (username_id) REFERENCES public.users(id) NOT VALID;


--
-- TOC entry 3213 (class 2606 OID 34731)
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3210 (class 2606 OID 34726)
-- Name: cards fki7eg9pr1nooc66s02ht1h3ew8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT fki7eg9pr1nooc66s02ht1h3ew8 FOREIGN KEY (deck_id) REFERENCES public.decks(id);
   
    



INSERT INTO users (username,password,email,level,experience,log_streak,gems,isenabled)VALUES('pramos4', '$2a$10$ar82Vt.BK15UZKvBEbqKo.elg1RUK30kqV4KPEF0pYKQ0xf3oVIEW','pramos4@gmail.com',0,0,0,0, TRUE);
INSERT INTO users (username,password,email,level,experience,log_streak,gems,isenabled)VALUES('pramos5', '$2a$10$APptla17oJ75u4ndIKopZ.mv9YOMl.X/ZtWYzrGmXwEMvv416DGqK','pramos5@gmail.com',0,0,0,0, TRUE);
INSERT INTO users (username,password,email,level,experience,log_streak,gems,isenabled)VALUES('pramos6', '$2a$10$GjoXO11UbM7ndaLwDgpSMOv2ML8/1MynLnVkIV4Nmfgejnp/wpEyG','pramos6@gmail.com',0,0,0,0, TRUE);

insert into user_roles (user_id, roles) values (1,'USER');
insert into user_roles (user_id, roles) values (1,'ADMIN');
insert into user_roles (user_id, roles) values (2,'USER');
insert into user_roles (user_id, roles) values (3,'USER');

insert into languages (language,image,userid) values ('Spanish','',null);
insert into languages (language,image,userid) values ('Akan','',null);
insert into languages (language,image,userid) values ('Amharic','',null);
insert into languages (language,image,userid) values ('Arabic','',null);
insert into languages (language,image,userid) values ('Assamese','',null);
insert into languages (language,image,userid) values ('Awadhi','',null);
insert into languages (language,image,userid) values ('Azerbaijani','',null);
insert into languages (language,image,userid) values ('Balochi','',null);
insert into languages (language,image,userid) values ('Belarusian','',null);
insert into languages (language,image,userid) values ('Bengali','',null);
insert into languages (language,image,userid) values ('Bhojpuri','',null);
insert into languages (language,image,userid) values ('Burmese','',null);
insert into languages (language,image,userid) values ('Cebuano (Visayan)','',null);
insert into languages (language,image,userid) values ('Chewa','',null);
insert into languages (language,image,userid) values ('Chhattisgarhi','',null);
insert into languages (language,image,userid) values ('Chittagonian','',null);
insert into languages (language,image,userid) values ('Czech','',null);
insert into languages (language,image,userid) values ('Deccan','',null);
insert into languages (language,image,userid) values ('Dhundhari','',null);
insert into languages (language,image,userid) values ('Dutch','',null);
insert into languages (language,image,userid) values ('Eastern Min','',null);
insert into languages (language,image,userid) values ('English','',null);
insert into languages (language,image,userid) values ('French','',null);
insert into languages (language,image,userid) values ('Fula','',null);
insert into languages (language,image,userid) values ('Gan Chinese','',null);
insert into languages (language,image,userid) values ('German','',null);
insert into languages (language,image,userid) values ('Greek','',null);
insert into languages (language,image,userid) values ('Gujarati','',null);
insert into languages (language,image,userid) values ('Haitian Creole','',null);
insert into languages (language,image,userid) values ('Hakka','',null);
insert into languages (language,image,userid) values ('Haryanvi','',null);
insert into languages (language,image,userid) values ('Hausa','',null);
insert into languages (language,image,userid) values ('Hiligaynon','',null);
insert into languages (language,image,userid) values ('Hindi','',null);
insert into languages (language,image,userid) values ('Hmong','',null);
insert into languages (language,image,userid) values ('Hungarian','',null);
insert into languages (language,image,userid) values ('Igbo','',null);
insert into languages (language,image,userid) values ('Ilocano','',null);
insert into languages (language,image,userid) values ('Italian','',null);
insert into languages (language,image,userid) values ('Japanese','',null);
insert into languages (language,image,userid) values ('Javanese','',null);
insert into languages (language,image,userid) values ('Jin','',null);
insert into languages (language,image,userid) values ('Kannada','',null);
insert into languages (language,image,userid) values ('Kazakh','',null);
insert into languages (language,image,userid) values ('Khmer','',null);
insert into languages (language,image,userid) values ('Kinyarwanda','',null);
insert into languages (language,image,userid) values ('Kirundi','',null);
insert into languages (language,image,userid) values ('Konkani','',null);
insert into languages (language,image,userid) values ('Korean','',null);
insert into languages (language,image,userid) values ('Kurdish','',null);
insert into languages (language,image,userid) values ('Madurese','',null);
insert into languages (language,image,userid) values ('Magahi','',null);
insert into languages (language,image,userid) values ('Maithili','',null);
insert into languages (language,image,userid) values ('Malagasy','',null);
insert into languages (language,image,userid) values ('Malay/Indonesian','',null);
insert into languages (language,image,userid) values ('Malayalam','',null);
insert into languages (language,image,userid) values ('Mandarin','',null);
insert into languages (language,image,userid) values ('Marathi','',null);
insert into languages (language,image,userid) values ('Marwari','',null);
insert into languages (language,image,userid) values ('Mossi','',null);
insert into languages (language,image,userid) values ('Nepali','',null);
insert into languages (language,image,userid) values ('Northern Min','',null);
insert into languages (language,image,userid) values ('Odia (Oriya)','',null);
insert into languages (language,image,userid) values ('Oromo','',null);
insert into languages (language,image,userid) values ('Pashto','',null);
insert into languages (language,image,userid) values ('Persian','',null);
insert into languages (language,image,userid) values ('Polish','',null);
insert into languages (language,image,userid) values ('Portuguese','',null);
insert into languages (language,image,userid) values ('Punjabi','',null);
insert into languages (language,image,userid) values ('Quechua','',null);
insert into languages (language,image,userid) values ('Romanian','',null);
insert into languages (language,image,userid) values ('Russian','',null);
insert into languages (language,image,userid) values ('Saraiki','',null);
insert into languages (language,image,userid) values ('Serbo-Croatian','',null);
insert into languages (language,image,userid) values ('Shona','',null);
insert into languages (language,image,userid) values ('Sindhi','',null);
insert into languages (language,image,userid) values ('Sinhalese','',null);
insert into languages (language,image,userid) values ('Somali','',null);
insert into languages (language,image,userid) values ('Southern Min','',null);
insert into languages (language,image,userid) values ('Sundanese','',null);
insert into languages (language,image,userid) values ('Swedish','',null);
insert into languages (language,image,userid) values ('Sylheti','',null);
insert into languages (language,image,userid) values ('Tagalog','',null);
insert into languages (language,image,userid) values ('Tamil','',null);
insert into languages (language,image,userid) values ('Telugu','',null);
insert into languages (language,image,userid) values ('Thai','',null);
insert into languages (language,image,userid) values ('Turkish','',null);
insert into languages (language,image,userid) values ('Turkmen','',null);
insert into languages (language,image,userid) values ('Ukrainian','',null);
insert into languages (language,image,userid) values ('Urdu','',null);
insert into languages (language,image,userid) values ('Uyghur','',null);
insert into languages (language,image,userid) values ('Uzbek','',null);
insert into languages (language,image,userid) values ('Vietnamese','',null);
insert into languages (language,image,userid) values ('Wu (inc. Shanghainese)','',null);
insert into languages (language,image,userid) values ('Xhosa','',null);
insert into languages (language,image,userid) values ('Xiang (Hunnanese)','',null);
insert into languages (language,image,userid) values ('Yoruba','',null);
insert into languages (language,image,userid) values ('Yue (Cantonese)','',null);
insert into languages (language,image,userid) values ('Zhuang','',null);
insert into languages (language,image,userid) values ('Zulu','',null);


insert into decks (name,description,username_id,language_id)
values ('palabras dificiles spa user 1','todas',2,1);
insert into decks (name,description,username_id,language_id)
values ('palabras dificiles eng user 2','todas',2,2);
insert into decks (name,description,username_id,language_id)
values ('palabras dificiles spa user 1','todas',3,1);
insert into decks (name,description,username_id,language_id)
values ('palabras dificiles eng user 2','todas',3,2);

insert into cards (enable,name1,name2,deck_id) 
values (true,'hello','Hola',1);
insert into cards (enable,name1,name2,deck_id) 
values (true,'aa','bb',2);
insert into cards (enable,name1,name2,deck_id) 
values (true,'cc','dd',3);
insert into cards (enable,name1,name2,deck_id) 
values (true,'ee','rr',4);

--alter table languages add column userId bigint;
--alter table languages alter column language set not null;
--alter table users add column isEnabled boolean;

CREATE TABLE public.confirmationtoken (
    token_id bigserial NOT null primary key,
    confirmation_token character varying(255),
    created_date timestamp without time zone,
    user_id bigint,
    CONSTRAINT fk_confirmationtoken_user
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE CASCADE
);

ALTER TABLE public.confirmationtoken OWNER TO user_db;

--
-- TOC entry 219 (class 1259 OID 34713)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.confirmationtoken_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.confirmationtoken_token_id_seq OWNER TO user_db;
